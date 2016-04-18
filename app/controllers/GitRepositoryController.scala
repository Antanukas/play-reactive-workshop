package controllers

import javax.inject.Inject

import models.{GitHubRepositoryId, GitRepository}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.UserService

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryController @Inject() (userService: UserService)(implicit exec: ExecutionContext) extends Controller {

  import models.JsonConverters._

  def search(query: String) = Action.async {
    Future {
      val data: Seq[GitRepository] = Seq(
        GitRepository(GitHubRepositoryId("11"), "Some repo", 5),
        GitRepository(GitHubRepositoryId("11"), "Some repo 2", 21),
        GitRepository(GitHubRepositoryId("11"), "Some repo 3", 44))
      Thread.sleep(5000)
      Ok(Json.toJson(data)) }
  }



}
