package controllers

import javax.inject._

import models.CreateUser
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (userService: UserService)(implicit exec: ExecutionContext) extends Controller {

  import models.JsonConverters._

  def get() = Action.async {
    userService.get().map(r => Ok(Json.toJson(r)))
  }

  def create() = Action.async(parse.json) { implicit req =>
    val body = req.body.as[CreateUser]
    userService.create(body).map(r => Ok(Json.toJson(r)))
  }
}
