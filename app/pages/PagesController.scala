package pages

import javax.inject._

import controllers.WebJarAssets
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class PagesController @Inject()(webJarAssets: WebJarAssets)(implicit ec: ExecutionContext) extends Controller {

  def index = Action {
    Ok(pages.html.index(webJarAssets))
  }

  def comments(owner: String, repoName: String) = Action {
    Ok(pages.html.comments(owner, repoName, webJarAssets))
  }

}
