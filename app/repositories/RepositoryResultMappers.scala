package repositories

import models.{User, UserId}
import org.joda.time.DateTime
import repositories.Models.CommentDbModel
import slick.jdbc.GetResult

object RepositoryResultMappers {

  implicit val commentResult = GetResult(r => CommentDbModel(
    id = r.nextLong,
    user = r.nextLong,
    repositoryOwner = r.nextString,
    repositoryName = r.nextString,
    comment = r.nextString,
    createdOn = new DateTime(r.nextTimestamp())))

  implicit val userResult = GetResult(r => User(id = UserId(r.nextLong()), username = r.nextString()))

  implicit val commentsWithUserResult = GetResult(r =>
    (commentResult(r), userResult(r)))
}
