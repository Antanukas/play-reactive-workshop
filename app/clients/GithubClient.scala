package clients

import clients.Models.{GithubRepositoriesResponse, GithubRepositoryResponse}
import com.google.inject.Inject
import play.Configuration
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import clients.Converters.{githubRepositoriesResponseRead, githubRepositoryOwnerRead, githubRepositoryResponseRead}


class GithubClient @Inject()(val ws: WSClient, val configuration: Configuration)(implicit ex: ExecutionContext) {

  private val searchEndpoint = configuration.getString("github-api.search-endpoint")
  private val singleRepoEndpoint = configuration.getString("github-api.single-repo-endpoint")
  private val token = Option(configuration.getString("github-api.token"))

  def searchRepositories(query: String): Future[GithubRepositoriesResponse]= {
    val responseFuture: Future[WSResponse] = wsurl(searchEndpoint)
      .withQueryString(("q", query))
      .get()

    responseFuture.map(res => res.json.as[GithubRepositoriesResponse])
  }

  def getRepository(owner: String, repoName: String): Future[GithubRepositoryResponse] = {
    val responseFuture: Future[WSResponse] = wsurl(s"$singleRepoEndpoint/$owner/$repoName").get()
    responseFuture.map(res => res.json.as[GithubRepositoryResponse])
  }

  private def wsurl(url: String): WSRequest = {
    val tokenQParams = token.map(token => Array(("access_token", token))).getOrElse(Array())
    ws.url(url).withQueryString(tokenQParams:_*)
  }
}
