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

    commentLikeRepository
      .insert(CommentLike(commentId = newCommentLike.commentId, userId = newCommentLike.userId))
      .zip(getGitHubRepositoryIdByCommentId(newCommentLike))
      .map { case (commentLike, gitHubRepositoryId) =>
        PublishableResult(commentLike, NewCommentLikeEvent(gitHubRepositoryId, commentLike.commentId))
      }
      .map(eventPublisher.publishEventsAndReturnResult)
  }

  def getLikes(commentId: CommentId): Future[Seq[CommentLike]] = db.run {
    commentLikeRepository.getLikes(commentId)
  }

  private def getGitHubRepositoryIdByCommentId(newCommentLike: NewCommentLike): DBIO[GitHubRepositoryId] = {
    commentRepository.getComment(newCommentLike.commentId)
      .map(comment => GitHubRepositoryId(comment.repositoryOwner, comment.repositoryName))
  }
}
