package repositories

import org.joda.time.DateTime


object Models {

  //TODO id as CommentId
  case class CommentDbModel(id: Long = -1, user: Long, repositoryOwner: String, repositoryName: String,
      comment: String, createdOn: DateTime = DateTime.now)

}
