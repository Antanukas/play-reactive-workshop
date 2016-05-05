package services

import clients.GitHubClient
import clients.Models.{GitHubRepositoriesResponse, GitHubRepositoryResponse}
import com.google.inject.Inject
import models.{GitHubRepositoryId, GitRepository}
import repositories.{CommentsRepository, JdbcProfileProvider}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryService @Inject()(
  gitHubClient: GitHubClient,
  commentService: CommentService,
  commentsRepository: CommentsRepository,
  jdbc: JdbcProfileProvider)(implicit ex: ExecutionContext) {

  //Slick implicits
  import jdbc.provider._

  def search(query: String): Future[Seq[GitRepository]] = {
    /*
     * Task: Search
     * 
     * 1. Implement private method getCommentsCount
     * 2. Implement private method fromGitHubResponse
     * 3. Implement private method fromGitHubRepositoriesResponses
     * 4. Use gitHubClient.searchRepositories and flatMap over fromGitHubRepositoriesResponses result
     */
    ???
  }

  private def fromGitHubRepositoriesResponses(response: GitHubRepositoriesResponse): Future[Seq[GitRepository]] = {
    /*
     * Task: Search
     *
     * 1. Map response.items using fromGitHubRepositoryResponse
     * 2. Use Future.sequence to convert Seq[Future] to Future[Seq]
     */
    ???
  }

  private def fromGitHubRepositoryResponse(response: GitHubRepositoryResponse): Future[GitRepository] = {
    /*
     * Task: Search
     * 
     * 1. First use getCommentCount.
     *  You can use helper `toGitHubId` to get GitHubRepositoryId from GitHubRepositoryResponse
     * 2. Use map on commentCount future and convert things to GitRepository
      *   Use `toGitRepository` which will do mapping for you
     */
    ???
  }

  private def getCommentCount(gitHubId: GitHubRepositoryId): Future[Long] = {
    /*
   * Task: Search
   * 
   * 1. Use commentsRepository.getCommentCount
   * 2. To convert DBIO to Future use `db.run` function
   */
    ???
  }


  def get(repoId: GitHubRepositoryId): Future[GitRepository] = {
    /*
     * Task: Display repository information
     * 
     * 1. Use gitHubClient.getRepository
     * 2. Use fromGitHubRepositoryResponse
     */
    ???
  }


  private def toGitRepository(repo: GitHubRepositoryResponse, commentCount: Long): GitRepository = {
    GitRepository(
      toGitHubId(repo),
      repo.name,
      repo.full_name,
      commentCount,
      repo.open_issues_count,
      repo.owner.avatar_url)
  }

  private def toGitHubId(repo: GitHubRepositoryResponse): GitHubRepositoryId = {
    GitHubRepositoryId(repo.owner.login, repo.name)
  }
}
