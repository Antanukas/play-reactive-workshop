package services

import com.google.inject.Inject
import models.{GitHubRepositoryId, GitRepository}

import scala.concurrent.{ExecutionContext, Future}

class GitRepositoryService @Inject()(implicit ex: ExecutionContext) {

  val data: Seq[GitRepository] = Seq(
    GitRepository(GitHubRepositoryId("1"), "Some repo", 5, 55),
    GitRepository(GitHubRepositoryId("2"), "Some repo 2", 21, 67),
    GitRepository(GitHubRepositoryId("3"), "Some repo 3", 44, 34))

  def search(query: String): Future[Seq[GitRepository]] = {
    //TODO integrate with github
    Future { Thread.sleep(1000); data; }
  }

  def get(repoId: GitHubRepositoryId): Future[GitRepository] = {
    //TODO integrate with github
    Future { data.find(_.id == repoId).getOrElse(throw new RuntimeException(s"Repository ${repoId.value} not found")) }
  }

}
