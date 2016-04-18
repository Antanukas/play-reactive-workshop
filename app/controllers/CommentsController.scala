package controllers

import java.time.ZonedDateTime
import javax.inject.Inject

import akka.stream.scaladsl.Source
import models.{Comment, GitHubRepositoryId, NewComment, UserId}
import play.api.http.{ContentTypes, MimeTypes}
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.Json
import play.api.libs.streams.Streams
import play.api.mvc.{Accepting, Action, Controller}

import scala.concurrent.{ExecutionContext, Future}

class CommentsController @Inject() ()(implicit exec: ExecutionContext) extends Controller {
  val EventStreamAccept = Accepting(MimeTypes.EVENT_STREAM)

  import models.JsonConverters._

  //Get these from db
  val comments = Seq(
    Comment(userId = UserId("1"), gitHubId = GitHubRepositoryId("4"), text = "Some nice message", createdOn = ZonedDateTime.now()),
    Comment(userId = UserId("2"), gitHubId = GitHubRepositoryId("4"), text = "Some message", createdOn = ZonedDateTime.now()),
    Comment(userId = UserId("3"), gitHubId = GitHubRepositoryId("4"), text = "OMG", createdOn = ZonedDateTime.now()))

  val (newCommentsOut, newCommentsChannel) = Concurrent.broadcast[NewComment]

  def get(githubId: GitHubRepositoryId) = Action.async { implicit req =>
    render.async {
      case Accepts.Json() => {
        //TODO remove. This is just for testing
        newCommentsChannel.push(NewComment(userId = UserId("3"), gitHubId = GitHubRepositoryId("4"), text = "OMG"))
        Future(comments).map(Json.toJson(_)).map(Ok(_))
      }
      case EventStreamAccept() => {
        val source = Source
          .fromPublisher(Streams.enumeratorToPublisher(newCommentsOut))
          .filter(comment => comment.gitHubId == githubId)
          .map { _ => comments} //TODO fetch from repo
          .map(Json.toJson(_))
        Future(Ok.chunked(source via EventSource.flow).as(ContentTypes.EVENT_STREAM))
      }
    }
  }

}
