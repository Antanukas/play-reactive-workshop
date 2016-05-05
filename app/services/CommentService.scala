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

  def getRepositoryComments(repoId: GitHubRepositoryId)
    (implicit currentUserId: Option[UserId]): Future[Seq[Comment]] = db.run {
    /*
     * Task: Get comment list
     *
     * 1. Implement commentsRepository.getCommentIdsByRepositoryId
     * 2. Implement getCommentsByIds
     * 3. Combine getCommentIdsByRepositoryId and getCommentsByIds to get Seq[Comment]
     */
    commentsRepository.getCommentIdsByRepositoryId(repoId).flatMap(getCommentsByIds)
  }

  private def getCommentsByIds(ids: Seq[CommentId])(implicit currentUserId: Option[UserId]): DBIO[Seq[Comment]] = {
    /*
     * Task: Get comment list
     *
     * 1. Use already implemented getComment
     * 2. Remember Future.sequence? DBIO.sequence does the same
     */
    DBIO.sequence(ids.map(id => getComment(id)))
  }

  def create(repoId: GitHubRepositoryId, newComment: NewComment)
    (implicit currentUserId: Option[UserId]): Future[Comment] = db.run {
     /*
      * Task: Create Comment
      *
      * 1. Implement insertNewComment
      * 2. Implement getComment
      * 3. Implement publishEvent
      * 4. Combine these three fns to get the result
      */
    insertNewComment(repoId, newComment)
      .flatMap(inserted => getComment(inserted.id))
      .map(publishEvent)
  }

  private def insertNewComment(repoId: GitHubRepositoryId, newComment: NewComment) = {
    /*
     * Task: Create Comment
     *
     * 1. use commentsRepository.insert
     * 2. fromNewCommentToDb to map data to Database Comment model
     */
    commentsRepository.insert(fromNewCommentToDb(repoId, newComment))
  }

  private def getComment(commentId: CommentId)(implicit currentUserId: Option[UserId]): DBIO[Comment] = {
    /*
     * Task: Create Comment
     *
     * 1. Use commentsRepository.getComment
     * 2. Then using its result use userRepository.getById
     * 3. finally use toApiComment(comment, user) to get final result
     */
    val withoutLikesImplementation: DBIO[Comment] = commentsRepository.getComment(commentId)
      .flatMap(comment => userRepository.getById(comment.user)
        .map(user => toApiComment(comment, user.get)))

    for {
      comment <- commentsRepository.getComment(commentId)
      likes <- commentLikeRepository.getLikes(commentId)
      user <- userRepository.getById(comment.user)
    } yield toApiComment(comment, user.get, likes)
  }

  private def publishEvent(comment: Comment): Comment = {
    /*
     * Task: Create Comment
     *
     * 1. Use eventPublished.publish to publish NewCommentEvent
     * 2. Return same comment to be able to nicely use this function in map
     */
    eventPublisher.publish(NewCommentEvent(comment.gitHubId, comment.id))
    comment
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

  def getRepositoryCommentsSource(repoId: GitHubRepositoryId)(implicit currentUserId: Option[UserId]):
  Source[Seq[Comment], NotUsed] = {
    eventPublisher.subscribe
      .filter {
        case event: NewCommentEvent => event.gitHubId == repoId
        case event: NewCommentLikeEvent => event.gitHubId == repoId;
      }
      .mapAsync(1) { _ => getRepositoryComments(repoId) }
  }

}
