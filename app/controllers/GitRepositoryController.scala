package controllers

import javax.inject.Inject

import models.{GitHubRepositoryId, GitRepository}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{GitRepositoryService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryController @Inject()(service: GitRepositoryService)
  (implicit exec: ExecutionContext) extends RestController {

  import models.JsonConverters._

  def search(query: String) = Action.async {
    service.search(query).map(toOkJson(_))
  }

  def get(repoId: GitHubRepositoryId) = Action.async {
    service.get(repoId).map(toOkJson(_))
  }


}
