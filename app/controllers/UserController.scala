package controllers

import javax.inject._

import models.LoginAttempt
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (userService: UserService)(implicit exec: ExecutionContext) extends RestController {

  import controllers.converters.JsonConverters._

  def login() = Action.async(parse.json) { implicit req =>
    val body = req.body.as[LoginAttempt]
    userService.login(body).map(toOkJson(_))
  }
}
