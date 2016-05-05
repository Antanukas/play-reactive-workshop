package clients

import clients.Models.{GithubRepositoryOwner, GitHubRepositoriesResponse, GitHubRepositoryResponse}
import models.GitRepository
import play.api.libs.json.Json


object Models {

  case class GitHubRepositoryResponse(id: Long, name: String, owner: GithubRepositoryOwner, full_name: String, open_issues_count: Int)
  case class GithubRepositoryOwner(login: String, avatar_url: String)
  case class GitHubRepositoriesResponse(items: Seq[GitHubRepositoryResponse])
}

object JsonConverters {

  implicit val githubRepositoryOwnerRead = Json.reads[GithubRepositoryOwner]
  implicit val githubRepositoryResponseRead = Json.reads[GitHubRepositoryResponse]
  implicit val githubRepositoriesResponseRead = Json.reads[GitHubRepositoriesResponse]
}


