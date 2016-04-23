package controllers

import play.api.libs.json.{Json, Writes}
import play.api.mvc.{Controller, Result}

trait RestController extends Controller {

  protected def toOkJson[T](obj: T)(implicit writes: Writes[T]): Result = Ok(Json.toJson(obj)(writes))
}
