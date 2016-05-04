package controllers

import com.google.inject.Inject
import models.{CommentId, NewCommentLike}
import play.api.mvc.Action
import services.CommentLikeService

import scala.concurrent.ExecutionContext

class CommentLikeController @Inject() (commentLikeService: CommentLikeService)
  (implicit exec: ExecutionContext) extends RestController {

  import controllers.converters.JsonConverters._

  def like(commentId: Long) = Action.async(parse.json) { implicit req =>
    val newLike = req.body.as[NewCommentLike]
    commentLikeService.like(newLike).map(toOkJson(_))
  }

  def getLikes(commentId: Long) = Action.async {
    commentLikeService.getLikes(CommentId(commentId)).map(toOkJson(_))
  }
}

