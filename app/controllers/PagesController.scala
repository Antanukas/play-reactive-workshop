package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PagesController @Inject()(webJarAssets: WebJarAssets)(implicit ec: ExecutionContext) extends Controller {

  def index = Action {
    Ok(views.html.index(webJarAssets))
  }

  def comments(repoId: String) = Action {
    Ok(views.html.comments(repoId, webJarAssets))
  }

}
