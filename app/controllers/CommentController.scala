package controllers

import javax.inject.Inject

import models._
import play.api.http.{ContentTypes, MimeTypes}
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.mvc.{Accepting, Action}
import services.CommentService

import scala.concurrent.{ExecutionContext, Future}

class CommentController @Inject() (commentService: CommentService)
  (implicit exec: ExecutionContext) extends RestController {

  val EventStreamAccept = Accepting(MimeTypes.EVENT_STREAM)

  import controllers.converters.JsonConverters._

  def get(owner: String, name: String, currentUserId: Option[Long]) = Action.async { implicit req =>
    val currentUserIdMapped = currentUserId.map(UserId(_))
    render.async {
      case Accepts.Json() =>
        commentService.getRepositoryComments(GitHubRepositoryId(owner, name))(currentUserIdMapped).map(toOkJson(_))
      case EventStreamAccept() =>
        val source = commentService
          .getRepositoryCommentsSource(GitHubRepositoryId(owner, name))(currentUserIdMapped)
          .map(Json.toJson(_))
        Future(Ok.chunked(source via EventSource.flow).as(ContentTypes.EVENT_STREAM))
    }
  }

  def create(owner: String, name: String) = Action.async(parse.json) { implicit req =>
    val newComment = req.body.as[NewComment]
    commentService.create(GitHubRepositoryId(owner, name), newComment)(Some(newComment.userId)).map(toOkJson(_))
  }
}
