package clients

import clients.Models.{GithubRepositoryOwner, GithubRepositoriesResponse, GithubRepositoryResponse}
import models.GitRepository
import play.api.libs.json.Json


object Models {

  case class GithubRepositoryResponse(id: Long, name: String, owner: GithubRepositoryOwner, full_name: String, open_issues_count: Int)
  case class GithubRepositoryOwner(login: String, avatar_url: String)
  case class GithubRepositoriesResponse(items: Seq[GithubRepositoryResponse])
}

object JsonConverters {

  implicit val githubRepositoryOwnerRead = Json.reads[GithubRepositoryOwner]
  implicit val githubRepositoryResponseRead = Json.reads[GithubRepositoryResponse]
  implicit val githubRepositoriesResponseRead = Json.reads[GithubRepositoriesResponse]
}


