package services

import clients.GithubClient
import clients.Models.GithubRepositoryResponse
import com.google.inject.Inject
import models.{GitHubRepositoryId, GitRepository}
import repositories.{JdbcProfileProvider, CommentsRepository}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryService @Inject()(
      githubClient: GithubClient,
      commentsRepository: CommentsRepository,
      jdbc: JdbcProfileProvider)(implicit ex: ExecutionContext) {

  //Slick implicits
  import jdbc.provider._
  import jdbc.provider.driver.api._

//  val data: Seq[GitRepository] = Seq(
//    GitRepository(GitHubRepositoryId("play", "play"), "Some repo", "Antanukas/Some repo", 5, 55, ""),
//    GitRepository(GitHubRepositoryId("spring", "spring"), "Some repo 2", "tieto/Some repo 2", 21, 67, ""),
//    GitRepository(GitHubRepositoryId("netflix", "hystrix"), "Some repo 3", "tieto/Some repo 3", 44, 34, ""))

  def search(query: String): Future[Seq[GitRepository]] = {
    githubClient.searchRepositories(query)
      .map(repos => repos.items.map(toGitRepository))
      .map(repos => DBIO.sequence(repos.map(enrichWithCommentCounts)))
      .flatMap(db.run(_))
  }

  def get(repoId: GitHubRepositoryId): Future[GitRepository] = {
    //TODO retrieve full name from repository and call github api
    githubClient.getRespository(repoId.owner, repoId.name)
      .map(toGitRepository)
  }

  private def enrichWithCommentCounts(repo: GitRepository): DBIO[GitRepository] = {
    commentsRepository.getForRepository(repo.id)
      .map(comments => repo.copy(commentCount = comments.length))
  }

  private def toGitRepository(repo: GithubRepositoryResponse): GitRepository = {
    GitRepository(
      GitHubRepositoryId(repo.owner.login, repo.name),
      repo.name,
      repo.full_name,
      0,
      repo.open_issues_count,
      repo.owner.avatar_url)
  }

}
