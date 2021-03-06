package repositories

import models.{CommentId, CommentLike, User, UserId}
import org.joda.time.DateTime
import repositories.Models.CommentDbModel
import slick.jdbc.GetResult

object RepositoryResultMappers {

  implicit val commentResult = GetResult(r => CommentDbModel(
    id = CommentId(r.nextLong),
    user = UserId(r.nextLong),
    repositoryOwner = r.nextString,
    repositoryName = r.nextString,
    comment = r.nextString,
    createdOn = new DateTime(r.nextTimestamp())))

  implicit val userResult = GetResult(r => User(id = UserId(r.nextLong()), username = r.nextString()))

  implicit val commentsWithUserResult = GetResult(r =>
    (commentResult(r), userResult(r)))

  implicit val commentLikeResult = GetResult(r => CommentLike(
    r.nextLong(), CommentId(r.nextLong()), UserId(r.nextLong())))

  implicit val commentIdResult = GetResult(r => CommentId(r.nextLong()))
}
