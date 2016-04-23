import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc.PathBindable

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
  case class NewComment(userId: UserId, gitHubId: GitHubRepositoryId, text: String)
  case class Comment(
    id: Long,
    userId: UserId,
    username: String,
    gitHubId: GitHubRepositoryId,
    text: String,
    createdOn: DateTime)
  case class NewCommentEvent(gitHubId: GitHubRepositoryId, commentId: Long) extends BusinessEvent

  //Repository
  case class GitHubRepositoryId(value: String) extends ModelId[String]
  case class GitRepository(id: GitHubRepositoryId, name: String, commentCount: Int, openIssueCount: BigDecimal)

  //Play specific things needed for models to be jsonified
  //TODO move away
  object JsonConverters {

    implicit val githubIdReads = idReads(GitHubRepositoryId(_))
    implicit val githubIdWrites = idWrites[GitHubRepositoryId]()
    implicit val userIdReads = idReads(UserId(_))
    implicit val userIdWrites = idWrites[UserId]()
    implicit val userFormat = Json.format[User]
    implicit val loginAttemptFormat = Json.format[LoginAttempt]
    implicit val gitRepositoryFormat = Json.format[GitRepository]
    implicit val commentFormat = Json.format[Comment]
    implicit val newCommentFormat = Json.format[NewComment]

    def idReads[T](constructor: String => T): Reads[T] = new Reads[T] {
      override def reads(json: JsValue): JsResult[T] = json match {
        case JsString(s) => JsSuccess(constructor(s))
        case _ => JsError("String value expected")
      }
    }

    def idWrites[T <: ModelId[String]](): Writes[T] = new Writes[T] {
      override def writes(o: T): JsValue = JsString(o.value)
    }
  }

  object Binders {
    implicit val userId: PathBindable[UserId] = new PathBindable[UserId] {
      override def bind(key: String, value: String) = Right(UserId(value))
      override def unbind(key: String, value: UserId) = value.value
    }

    implicit val githubRepositoryId: PathBindable[GitHubRepositoryId] = new PathBindable[GitHubRepositoryId] {
      override def bind(key: String, value: String) = Right(GitHubRepositoryId(value))
      override def unbind(key: String, value: GitHubRepositoryId) = value.value
    }
  }
}
