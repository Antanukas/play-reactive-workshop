import org.joda.time.DateTime

package object models {

  trait BusinessEvent
  trait ModelId[T] {
    def value: T
  }

  //User
  case class UserId(value: Long) extends ModelId[Long]
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
    createdOn: DateTime = DateTime.now,
    //Should be implemented using likes
    isUserLiked: Boolean = false,
    likeCount: Int = 0)
  case class NewCommentEvent(gitHubId: GitHubRepositoryId, commentId: CommentId) extends BusinessEvent

  //Likes
  case class NewCommentLikeEvent(gitHubId: GitHubRepositoryId, commentId: CommentId) extends BusinessEvent
  case class NewCommentLike(commentId: CommentId, userId: UserId)
  case class CommentLike(id: Long = -1, commentId: CommentId, userId: UserId)

  //Repository
  case class GitHubRepositoryId(owner: String, name: String)
  case class GitRepository(
    id: GitHubRepositoryId,
    name: String,
    fullName: String,
    commentCount: Long,
    openIssueCount: BigDecimal,
    avatarUrl: String)
}
