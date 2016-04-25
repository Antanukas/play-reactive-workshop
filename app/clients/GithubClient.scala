package clients

import com.google.inject.Inject
import models.{GitHubRepositoryId, GitRepository}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.{ExecutionContext, Future}


class GithubClient @Inject()(val ws: WSClient)(implicit ex: ExecutionContext) {

  import Converters.githubRepositoryRead

  private val searchEndpoint: String => String = query => s"https://api.github.com/search/repositories?q=$query"
  private val repoByNameEndpoint: String => String = fullName => s"https://api.github.com/repos/$fullName"

  def searchRepositories(query: String): Future[Seq[GitRepository]]= {
      val responseFuture: Future[WSResponse] = ws.url(searchEndpoint(query)).get()
      responseFuture
        .map(res => (res.json \ "items").as[Seq[GitRepository]])
  }

  def getRespository(fullName: String): Future[GitRepository] = {
    val responseFuture: Future[WSResponse] = ws.url(repoByNameEndpoint(fullName)).get()
    responseFuture
      .map(res => res.json.as[GitRepository])
  }
}

object Converters {

  implicit val githubRepositoryRead: Reads[GitRepository] = (
   (__ \ "id").read[Int].map(i => GitHubRepositoryId(i.toString)) and
   (__ \ "name").read[String] and
   (__ \ "full_name").read[String] and
   Reads(value => JsSuccess(0)) and
   (__ \ "open_issues_count").read[BigDecimal]
 )(GitRepository.apply _)

}
