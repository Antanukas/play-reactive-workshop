package services

import com.google.inject.Inject
import models.{CreateUser, User, UserId}
import repositories.{JdbcProfileProvider, UserRepository}

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  jdbc: JdbcProfileProvider,
  userRepository: UserRepository)(implicit ec: ExecutionContext) {

  //Needed for db.run
  import jdbc.provider._

  def get(): Future[Seq[User]] = db.run(userRepository.get)

  def create(createUser: CreateUser): Future[User] = db.run {
    userRepository.insert(createUser.username).map(id => User(UserId(id.toString), createUser.username))
  }
}
