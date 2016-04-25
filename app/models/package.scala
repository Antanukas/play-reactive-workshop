import org.joda.time.DateTime

package object models {

  trait BusinessEvent
  trait ModelId[T] {
    def value: T
  }

  //User
  case class UserId(value: String) extends ModelId[String]
  case class User(id: UserId, username: String)
  case class LoginAttempt(username: String)

  //Comments
  case class CommentId(value: Long) extends ModelId[Long]
  case class NewComment(userId: UserId, gitHubId: GitHubRepositoryId, text: String)
  case class Comment(
    id: CommentId,
    userId: UserId,
    username: String,
    gitHubId: GitHubRepositoryId,
    text: String,
    createdOn: DateTime)
  case class NewCommentEvent(gitHubId: GitHubRepositoryId, commentId: CommentId) extends BusinessEvent

  //Repository
  case class GitHubRepositoryId(value: String) extends ModelId[String]
  case class GitRepository(id: GitHubRepositoryId, name: String, fullName: String, commentCount: Int, openIssueCount: BigDecimal)
}
