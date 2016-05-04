package services

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import models._
import org.joda.time.DateTime
import repositories.Models.CommentDbModel
import repositories.{CommentLikeRepository, CommentsRepository, JdbcProfileProvider, UserRepository}
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class CommentService @Inject()(
        eventPublisher: EventPublisher,
        commentsRepository: CommentsRepository,
        userRepository: UserRepository,
        commentLikeRepository: CommentLikeRepository,
        jdbc: JdbcProfileProvider)(implicit exec: ExecutionContext) {

  //Slick implicits for db.run
  import jdbc.provider._

  def getRepositoryComments(repoId: GitHubRepositoryId)(implicit currentUserId: Option[UserId]): Future[Seq[Comment]] = db.run {
    commentsRepository.getCommentIdsByRepositoryId(repoId)
      .map(ids => ids.map(id => getComment(id)))
      .flatMap(DBIO.sequence(_))
  }

  def create(repoId: GitHubRepositoryId, newComment: NewComment)(implicit currentUserId: Option[UserId]): Future[Comment] = db.run {
    commentsRepository.insert(fromNewCommentToDb(repoId, newComment))
      .flatMap(inserted => getComment(inserted.id))
      .map(comment => PublishableResult(comment, NewCommentEvent(repoId, comment.id)))
      .map(eventPublisher.publishEventsAndReturnResult)
  }

  private def getComment(commentId: CommentId)(implicit currentUserId: Option[UserId]): DBIO[Comment] = {
    for {
      comment <- commentsRepository.getComment(commentId)
      likes <- commentLikeRepository.getLikes(commentId)
      user <- userRepository.getById(comment.user)
    } yield toApiComment(comment, user.get, likes)
  }

  private def toApiComment(
    commentDbModel: CommentDbModel,
    user: User,
    //Like functionality
    commentLikes: Seq[CommentLike] = Seq())(implicit currentUserId: Option[UserId]): Comment = {

    val isUserLiked = commentLikes.exists(currentUserId.isDefined && _.userId == currentUserId.get)
    Comment(
      commentDbModel.id,
      commentDbModel.user,
      user.username,
      GitHubRepositoryId(commentDbModel.repositoryOwner, commentDbModel.repositoryName),
      commentDbModel.comment,
      commentDbModel.createdOn,
      isUserLiked = isUserLiked,
      likeCount = commentLikes.size)
  }

  private def fromNewCommentToDb(repo: GitHubRepositoryId, newComment: NewComment) = {
    CommentDbModel(
      user = newComment.userId,
      repositoryOwner = repo.owner,
      repositoryName = repo.name,
      comment = newComment.text,
      createdOn = DateTime.now)
  }

  def getRepositoryCommentsSource(repoId: GitHubRepositoryId)(implicit currentUserId: Option[UserId]): Source[Seq[Comment], NotUsed] = {
    eventPublisher.subscribe
      .filter {
        case event: NewCommentEvent => event.gitHubId == repoId
        case event: NewCommentLikeEvent => event.gitHubId == repoId;
      }
      .mapAsync(1) { _ => getRepositoryComments(repoId) }
  }

}
