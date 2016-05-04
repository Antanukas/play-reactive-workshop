package repositories

import com.google.inject.Inject
import models.{User, UserId}
import slick.dbio
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

class UserRepository @Inject() (jdbc: JdbcProfileProvider)(implicit ec: ExecutionContext) {

  import jdbc.provider.driver.api._
  import RepositoryResultMappers._

  def get(): dbio.DBIO[Seq[User]] =
    sql""" select * from "users" """.as[User]

  def insert(username: String): dbio.DBIO[Int] =
    sqlu"""insert into "users"("username") values (${username})"""
      .flatMap { _ => sql"SELECT LASTVAL()".as[Int].head }

  def getById(userId: UserId): dbio.DBIO[Option[User]] =
    sql""" select * from "users" where "id" = ${userId.value}""".as[User].headOption

  def find(username: String): dbio.DBIO[Option[User]] =
    sql""" select * from "users" where "username" = ${username}""".as[User].headOption
}
