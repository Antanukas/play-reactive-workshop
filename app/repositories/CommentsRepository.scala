package repositories

import com.google.inject.Inject
import models.{User, UserId, GitHubRepositoryId}
import org.joda.time.DateTime
import repositories.Models.{CommentDbModel}
import slick.dbio
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

class CommentsRepository @Inject() (jdbc: JdbcProfileProvider)(implicit ec: ExecutionContext) {

  import jdbc.provider.driver.api._

  implicit val userResult = GetResult(r => CommentDbModel(
    id = r.nextLong,
    user = r.nextLong,
    repositoryOwner = r.nextString,
    repositoryName = r.nextString,
    comment = r.nextString,
    createdOn = new DateTime(r.nextDate()),
    username = r.nextString))

  def getForRepository(repository: GitHubRepositoryId): dbio.DBIO[Seq[CommentDbModel]] =
    sql""" select c.*, u."username" from "comments" c join "users" u on c."user" = u."id" where "repository_owner" = ${repository.owner} and "repository_name" = ${repository.name} """
      .as[CommentDbModel]

  def insert(comment: CommentDbModel): dbio.DBIO[Int] =
    sqlu"""insert into "comments"("user", "repository_owner", "repository_name", "comment") values (${comment.user}, ${comment.repositoryOwner}, ${comment.repositoryName}, ${comment.comment})"""
      .flatMap { _ => sql"SELECT LASTVAL()".as[Int].head }

}
