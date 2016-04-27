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
      case Some(user) => DBIO.successful(user)
      case None => insertUser(loginRequest.username)
    }
    .flatMap(u => userRepository.find(u.username))  // we need to retrieve inserted id separately
    .flatMap {
      case Some(user) => DBIO.successful(user)
      case None => DBIO.failed(new RuntimeException(s"User ${loginRequest.username} login failed"))
    }.transactionally
  }

  private def insertUser(username: String): DBIO[User] = {
    userRepository.insert(username).map(_ => User(UserId(""), username))
  }
}
