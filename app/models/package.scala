import play.api.libs.json.Json

package object models {

  //User models
  type UserId = String
  case class User(id: UserId, username: String)
  case class CreateUser(username: String)


  case class Tweet(userId: UserId, text: String)

  object JsonConverters {
    implicit val user = Json.format[User]
    implicit val createUser = Json.format[CreateUser]
    implicit val tweet = Json.format[Tweet]
  }
}
