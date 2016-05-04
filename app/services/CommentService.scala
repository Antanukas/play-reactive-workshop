package services

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import models._
import org.joda.time.DateTime
import repositories.Models.CommentDbModel
import repositories.{CommentLikeRepository, CommentsRepository, JdbcProfileProvider, UserRepository}
import slick.dbio.Effect.All
import slick.dbio.{DBIO, DBIOAction, NoStream}

import scala.concurrent.{ExecutionContext, Future}

class CommentService @Inject()(
        eventPublisher: EventPublisher,
        commentsRepository: CommentsRepository,
        userRepository: UserRepository,
        commentLikeRepository: CommentLikeRepository,
        jdbc: JdbcProfileProvider)(implicit exec: ExecutionContext) {

  //Slick implicits for db.run
  import jdbc.provider._

  def getRepositoryComments(repoId: GitHubRepositoryId)(implicit currentUserId: UserId): Future[Seq[Comment]] = db.run {
    commentsRepository.getCommentIdsByRepositoryId(repoId)
      .map(ids => ids.map(id => getComment(CommentId(id))))
      .flatMap(DBIO.sequence(_))
  }

  def create(repoId: GitHubRepositoryId, newComment: NewComment)(implicit currentUserId: UserId): Future[Comment] = db.run {
    commentsRepository.insert(fromNewCommentToDb(repoId, newComment))
      .flatMap(inserted => getComment(CommentId(inserted.id)))
      .map(comment => PublishableResult(comment, NewCommentEvent(repoId, comment.id)))
      .map(eventPublisher.publishEventsAndReturnResult)
  }

  private def getComment(commentId: CommentId)(implicit currentUserId: UserId): DBIO[Comment] = {
    for {
      comment <- commentsRepository.getComment(commentId)
      likes <- commentLikeRepository.getLikes(commentId)
      user <- userRepository.getById(UserId(comment.user))
    } yield toApiComment(comment, user.get, likes)
  }

  private def toApiComment(
    commentDbModel: CommentDbModel,
    user: User,
    //Like functionality
    commentLikes: Seq[CommentLike] = Seq())(implicit currentUserId: UserId): Comment = {

    val isUserLiked = commentLikes.exists(_.userId == currentUserId)
    Comment(
      CommentId(commentDbModel.id),
      UserId(commentDbModel.user),
      user.username,
      GitHubRepositoryId(commentDbModel.repositoryOwner, commentDbModel.repositoryName),
      commentDbModel.comment,
      commentDbModel.createdOn,
      isUserLiked = isUserLiked,
      likeCount = commentLikes.size)
  }

  private def fromNewCommentToDb(repo: GitHubRepositoryId, newComment: NewComment) = {
    CommentDbModel(
      user = newComment.userId.value,
      repositoryOwner = repo.owner,
      repositoryName = repo.name,
      comment = newComment.text,
      createdOn = DateTime.now)
  }

  def getRepositoryCommentsSource(repoId: GitHubRepositoryId)(implicit currentUserId: UserId): Source[Seq[Comment], NotUsed] = {
    eventPublisher.subscribe
      .filter {
        case event: NewCommentEvent => event.gitHubId == repoId
        case event: NewCommentLikeEvent => event.gitHubId == repoId;
      }
      .mapAsync(1) { _ => getRepositoryComments(repoId) }
  }

}
