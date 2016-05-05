package controllers

import com.google.inject.Inject
import models.{CommentId, NewCommentLike}
import play.api.mvc.Action
import services.CommentLikeService

import scala.concurrent.ExecutionContext

class CommentLikeController @Inject() (commentLikeService: CommentLikeService)
  (implicit exec: ExecutionContext) extends RestController {

  import controllers.converters.JsonConverters._

  /*
   * Task: Comment Like
   *
   * 1. Create 3.sql and create table for mapping models.NewCommentLike
   * 2. Implement commentLikeService.like and commentLikeService.getLikes
   * 3. Improve getComment to additionally fetch 2 fields: comment count and isUserLiked
   */
  def like(commentId: Long) = Action.async(parse.json) { implicit req =>
    val newLike = req.body.as[NewCommentLike]
    commentLikeService.like(newLike).map(toOkJson(_))
  }

  def getLikes(commentId: Long) = Action.async {
    commentLikeService.getLikes(CommentId(commentId)).map(toOkJson(_))
  }
}

