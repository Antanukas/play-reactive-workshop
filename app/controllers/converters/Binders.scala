package controllers.converters

import models.{CommentId, GitHubRepositoryId, UserId}
import play.api.mvc.PathBindable

object Binders {

  implicit val userId: PathBindable[UserId] = new PathBindable[UserId] {
    override def bind(key: String, value: String) = Right(UserId(value))
    override def unbind(key: String, value: UserId) = value.value
  }

  implicit val commentId: PathBindable[CommentId] = new PathBindable[CommentId] {
    override def bind(key: String, value: String) = Right(CommentId(value.toLong))
    override def unbind(key: String, value: CommentId) = value.value.toString
  }
}
