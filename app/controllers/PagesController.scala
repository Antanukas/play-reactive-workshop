package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PagesController @Inject()(webJarAssets: WebJarAssets)(implicit ec: ExecutionContext) extends Controller {

  def index = Action {
    Ok(pages.html.index(webJarAssets))
  }

  def comments(owner: String, repoName: String) = Action {
    Ok(pages.html.comments(owner, repoName, webJarAssets))
  }

}
