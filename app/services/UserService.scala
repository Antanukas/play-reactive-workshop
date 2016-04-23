package services

import com.google.inject.Inject
import models.{LoginAttempt, User, UserId}
import repositories.{JdbcProfileProvider, UserRepository}
import slick.dbio.DBIOAction

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  jdbc: JdbcProfileProvider,
  userRepository: UserRepository)(implicit ec: ExecutionContext) {

  //Slick implicits
  import jdbc.provider._
  import jdbc.provider.driver.api._

  def login(loginRequest: LoginAttempt): Future[User] = db.run {
    //Naive implementation which creates user if such doesn't exist yet
    userRepository.find(loginRequest.username).flatMap {
      case Some(user) => DBIOAction.successful(user)
      case None => insertUser(loginRequest.username)
    }.transactionally
  }

  private def insertUser(username: String): DBIO[User] = {
    userRepository.insert(username).map(id => User(UserId(id.toString), username))
  }
}
