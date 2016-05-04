package repositories

import models.{CommentId, UserId}
import org.joda.time.DateTime


object Models {

  case class CommentDbModel(
    id: CommentId = CommentId(-1),
    user: UserId,
    repositoryOwner: String,
    repositoryName: String,
    comment: String,
    createdOn: DateTime = DateTime.now)

}
