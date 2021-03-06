package controllers.converters

import models._
import play.api.libs.json.{JsError, JsSuccess, Writes, _}

object JsonConverters {

  implicit val userIdReads = idReads(jsonValue => UserId(jsonValue.toLong))
  implicit val userIdWrites = idWrites[Long, UserId]()
  implicit val commendIdReads = idReads(jsonValue => CommentId(jsonValue.toLong))
  implicit val commentIdWrites = idWrites[Long, CommentId]()
  implicit val githubRepoIdFormat = Json.format[GitHubRepositoryId]
  implicit val userFormat = Json.format[User]
  implicit val loginAttemptFormat = Json.format[LoginAttempt]
  implicit val gitRepositoryFormat = Json.format[GitRepository]
  implicit val commentFormat = Json.format[Comment]
  implicit val newCommentFormat = Json.format[NewComment]

  implicit val newCommentLikeFormat = Json.format[NewCommentLike]
  implicit val commentLikeFormat = Json.format[CommentLike]

  def idReads[T](constructor: String => T): Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = json match {
      case JsString(s) => JsSuccess(constructor(s))
      case _ => JsError("String value expected")
    }
  }

  def idWrites[ID, T <: ModelId[ID]](): Writes[T] = new Writes[T] {
    override def writes(o: T): JsValue = JsString(o.value.toString)
  }
}
