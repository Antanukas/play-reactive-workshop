package repositories

import com.google.inject.Inject
import models.{User, UserId, GitHubRepositoryId}
import org.joda.time.DateTime
import repositories.Models.{CommentDbModel}
import slick.dbio
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

class CommentsRepository @Inject() (
  jdbc: JdbcProfileProvider)(implicit ec: ExecutionContext) {

  import jdbc.provider.driver.api._
  import RepositoryResultMappers._

  def getCommentCount(repository: GitHubRepositoryId): dbio.DBIO[Long] = {
    sql"""select count(*) from "comments"
          where "repository_owner" = ${repository.owner} and "repository_name" = ${repository.name} """
      .as[Long].head
  }

  def getForRepository(repository: GitHubRepositoryId): dbio.DBIO[Seq[(CommentDbModel, User)]] = {
    sql""" select c.*, u.* from "comments" c join "users" u on c."user" = u."id"
           where "repository_owner" = ${repository.owner} and "repository_name" = ${repository.name}
           order by c."created_on" desc """
      .as[(CommentDbModel, User)]
  }

  def insert(comment: CommentDbModel): dbio.DBIO[CommentDbModel] = {
    sqlu"""insert into "comments"("user", "repository_owner", "repository_name", "comment")
          values (${comment.user}, ${comment.repositoryOwner}, ${comment.repositoryName}, ${comment.comment})"""
      .flatMap { _ => sql"SELECT LASTVAL()".as[Int].head }
      .map(id => comment.copy(id = id))
  }

}
