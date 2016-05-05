package services

import javax.inject.Inject

import models.{CommentId, CommentLike, _}
import repositories.{CommentLikeRepository, CommentsRepository, JdbcProfileProvider}
import slick.dbio.DBIO

import scala.concurrent.{ExecutionContext, Future}

class CommentLikeService @Inject()(
  eventPublisher: EventPublisher,
  commentLikeRepository: CommentLikeRepository,
  commentRepository: CommentsRepository,
  jdbc: JdbcProfileProvider)(implicit exec: ExecutionContext) {

  //Slick implicits for db.run
  import jdbc.provider._


  def like(newCommentLike: NewCommentLike): Future[CommentLike] = db.run {
  /*
   * Task: Comment Like
   *
   * 1. Save comment like using commentLikeRepository. Implement insert.
   * 2. implement getGitHubRepositoryIdByCommentId by commentID
   * 3. Publish NewCommentLikeEvent
   */
    commentLikeRepository
      .insert(CommentLike(commentId = newCommentLike.commentId, userId = newCommentLike.userId))
      .zip(getGitHubRepositoryIdByCommentId(newCommentLike.commentId))
      .map { case (commentLike, gitHubRepositoryId) =>
        publishEvent(commentLike, gitHubRepositoryId)
      }
  }

  def getLikes(commentId: CommentId): Future[Seq[CommentLike]] = db.run {
    commentLikeRepository.getLikes(commentId)
  }

  private def getGitHubRepositoryIdByCommentId(commentId: CommentId): DBIO[GitHubRepositoryId] = {
    commentRepository.getComment(commentId)
      .map(comment => GitHubRepositoryId(comment.repositoryOwner, comment.repositoryName))
  }

  private def publishEvent(commentLike: CommentLike, gitHubRepositoryId: GitHubRepositoryId): CommentLike = {
    eventPublisher.publish(NewCommentLikeEvent(gitHubRepositoryId, commentLike.commentId))
    commentLike
  }
}
