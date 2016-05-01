package services

import clients.GithubClient
import clients.Models.GithubRepositoryResponse
import com.google.inject.Inject
import models.{GitHubRepositoryId, GitRepository}
import repositories.{JdbcProfileProvider, CommentsRepository}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryService @Inject()(
      githubClient: GithubClient,
      commentService: CommentService,
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
    githubClient.searchRepositories(query).flatMap { repos =>
      Future.sequence(repos.items.map(fromGitHubResponse))
    }
  }

  def get(repoId: GitHubRepositoryId): Future[GitRepository] = {
    //TODO retrieve full name from repository and call github api
    githubClient.getRepository(repoId.owner, repoId.name).flatMap(fromGitHubResponse)
  }

  private def fromGitHubResponse(githubRepositoryResponse: GithubRepositoryResponse): Future[GitRepository] = {
    getCommentCount(toGitHubId(githubRepositoryResponse))
      .map(commentCount => toGitRepository(githubRepositoryResponse, commentCount))
  }

  private def getCommentCount(gitHubId: GitHubRepositoryId): Future[Long] =
    db.run(commentsRepository.getCommentCount(gitHubId))

  private def toGitRepository(repo: GithubRepositoryResponse, commentCount: Long): GitRepository = {
    GitRepository(
      toGitHubId(repo),
      repo.name,
      repo.full_name,
      commentCount,
      repo.open_issues_count,
      repo.owner.avatar_url)
  }

  private def toGitHubId(repo: GithubRepositoryResponse): GitHubRepositoryId = {
    GitHubRepositoryId(repo.owner.login, repo.name)
  }
}
