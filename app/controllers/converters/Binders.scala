package controllers.converters

import models.{CommentId, UserId}
import play.api.mvc.PathBindable

object Binders {

  implicit val userId: PathBindable[UserId] = new PathBindable[UserId] {
    override def bind(key: String, value: String) = Right(UserId(value.toLong))
    override def unbind(key: String, value: UserId) = value.value.toString
  }

  implicit val commentId: PathBindable[CommentId] = new PathBindable[CommentId] {
    override def bind(key: String, value: String) = Right(CommentId(value.toLong))
    override def unbind(key: String, value: CommentId) = value.value.toString
  }
}
