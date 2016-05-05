package repositories

import com.google.inject.Inject
import models._
import org.joda.time.DateTime
import repositories.Models.CommentDbModel
import slick.dbio
import slick.jdbc.GetResult

import scala.concurrent.ExecutionContext

class CommentsRepository @Inject() (
  jdbc: JdbcProfileProvider)(implicit ec: ExecutionContext) {

  import jdbc.provider.driver.api._
  import RepositoryResultMappers._

  def getComment(commentId: CommentId): dbio.DBIO[CommentDbModel] = {
    sql""" select * from "comments" where "id" = ${commentId.value} """.as[CommentDbModel].head
  }

  def getCommentCount(repository: GitHubRepositoryId): dbio.DBIO[Long] = {
    sql"""select count(*) from "comments"
          where "repository_owner" = ${repository.owner} and "repository_name" = ${repository.name} """
      .as[Long].head
  }

  def getCommentIdsByRepositoryId(repository: GitHubRepositoryId): dbio.DBIO[Seq[CommentId]] = {
    /*
     * Task: Get comment list
     *
     * 1. Write a SQL query to get all comment ids by repository id
     * 2. Check 2.sql file to see how comments table look.
     *    You should be interested in repository_owner and repository_name
     * 3. Checkout other queries for examples
     */
    ???
  }

  def insert(comment: CommentDbModel): dbio.DBIO[CommentDbModel] = {
    sqlu"""insert into "comments"("user", "repository_owner", "repository_name", "comment")
          values (${comment.user.value}, ${comment.repositoryOwner}, ${comment.repositoryName}, ${comment.comment})"""
      .flatMap { _ => sql"SELECT LASTVAL()".as[Int].head }
      .map(id => comment.copy(id = CommentId(id)))
  }
}
