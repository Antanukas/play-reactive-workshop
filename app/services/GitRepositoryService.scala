package services

import clients.GithubClient
import com.google.inject.Inject
import models.{GitRepositorySearchResult, GitHubRepositoryId, GitRepository}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryService @Inject()(githubClient: GithubClient)(implicit ex: ExecutionContext) {

  val data: Seq[GitRepository] = Seq(
    GitRepository(GitHubRepositoryId("1"), "Some repo", "Antanukas/Some repo", 5, 55),
    GitRepository(GitHubRepositoryId("2"), "Some repo 2", "tieto/Some repo 2", 21, 67),
    GitRepository(GitHubRepositoryId("3"), "Some repo 3", "tieto/Some repo 3", 44, 34))

  def search(query: String): Future[Seq[GitRepository]] = {
    githubClient.searchRepositories(query)
  }

  def get(repoId: GitHubRepositoryId): Future[GitRepository] = {
    //TODO retrieve full name from repository and call github api
    githubClient.getRespository("play/play")
  }

}
