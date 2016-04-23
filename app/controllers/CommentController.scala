package controllers

import javax.inject.Inject

import models.{GitHubRepositoryId, NewComment}
import play.api.http.{ContentTypes, MimeTypes}
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.mvc.{Accepting, Action}
import services.CommentService

import scala.concurrent.{ExecutionContext, Future}

class CommentController @Inject() (commentService: CommentService)
  (implicit exec: ExecutionContext) extends RestController {

  val EventStreamAccept = Accepting(MimeTypes.EVENT_STREAM)

  import models.JsonConverters._

  def get(repoId: GitHubRepositoryId) = Action.async { implicit req =>
    render.async {
      case Accepts.Json() =>
        commentService.getRepositoryComments(repoId).map(toOkJson(_))
      case EventStreamAccept() =>
        val source = commentService.getRepositoryCommentsSource(repoId).map(Json.toJson(_))
        Future(Ok.chunked(source via EventSource.flow).as(ContentTypes.EVENT_STREAM))
    }
  }

  def create(repoId: GitHubRepositoryId) = Action.async(parse.json) { implicit req =>
    val newComment = req.body.as[NewComment]
    commentService.create(repoId, newComment).map(toOkJson(_))
  }
}
