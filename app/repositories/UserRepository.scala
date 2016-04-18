package repositories

import com.google.inject.Inject
import models.{User, UserId}
import slick.dbio.DBIO
import slick.driver.PostgresDriver.api._
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

class UserRepository @Inject() ()(implicit ec: ExecutionContext) {

  implicit val userResult = GetResult(r => User(id = UserId(r.nextLong().toString), username = r.nextString()))

  def get(): DBIO[Seq[User]] = sql""" select * from "users" """.as[User]
  def insert(username: String): DBIO[Int] = sqlu"""insert into "users"("username") values (${username})"""

}
