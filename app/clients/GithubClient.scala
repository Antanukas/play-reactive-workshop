package clients

import clients.Models.{GithubRepositoriesResponse, GithubRepositoryResponse}
import com.google.inject.Inject
import play.Configuration
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import clients.Converters.{githubRepositoriesResponseRead, githubRepositoryResponseRead, githubRepositoryOwnerRead}


class GithubClient @Inject()(val ws: WSClient, val configuration: Configuration)(implicit ex: ExecutionContext) {

  private val searchEndpoint = configuration.getString("github-api.search-endpoint")
  private val singleRepoEndpoint = configuration.getString("github-api.single-repo-endpoint")
  private val token = Option.apply(configuration.getString("github-api.token"))

  def searchRepositories(query: String): Future[GithubRepositoriesResponse]= {
    println(token)
    val responseFuture: Future[WSResponse] = ws.url(searchEndpoint)
      .withQueryString(("q", query))
      .get()

    responseFuture
      .map(res => res.json.as[GithubRepositoriesResponse])
  }

  def getRespository(owner: String, repoName: String): Future[GithubRepositoryResponse] = {
    println(s"$singleRepoEndpoint/$owner/$repoName")
    val responseFuture: Future[WSResponse] = ws.url(s"$singleRepoEndpoint/$owner/$repoName")
      .get()
    responseFuture
      .map(res => res.json.as[GithubRepositoryResponse])
  }
}
