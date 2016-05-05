package controllers

import javax.inject.Inject

import models.{GitHubRepositoryId, GitRepository}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{GitRepositoryService, UserService}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryController @Inject()(service: GitRepositoryService)
  (implicit exec: ExecutionContext) extends RestController {

  import controllers.converters.JsonConverters._

  def search(query: String) = Action.async {
    /*
     * Task: Search
     *
     * Implement GitRepositoryService.search
     */
    service.search(query).map(toOkJson(_))
  }

  def get(owner: String, name: String) = Action.async {
    /*
     * Task: Display repository information
     *
     * Implement GitRepositoryService.get
     */
    service.get(GitHubRepositoryId(owner, name)).map(toOkJson(_))
  }
}
