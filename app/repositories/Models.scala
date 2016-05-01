package repositories

import org.joda.time.DateTime


object Models {

  case class CommentDbModel(id: Long, user: Long, repositoryOwner: String, repositoryName: String,
      comment: String, createdOn: DateTime = DateTime.now)

}
