package repositories

import com.google.inject.Inject
import models.{CommentId, CommentLike}
import slick.dbio

import scala.concurrent.ExecutionContext

class CommentLikeRepository @Inject() (
  jdbc: JdbcProfileProvider)(implicit ec: ExecutionContext) {

  import RepositoryResultMappers._
  import jdbc.provider.driver.api._

  def insert(commentLike: CommentLike): dbio.DBIO[CommentLike] = {
    sqlu"""insert into "comment_likes"("user", "comment")
           values (${commentLike.userId.value}, ${commentLike.commentId.value})"""
      .flatMap { _ => sql"SELECT LASTVAL()".as[Int].head }
      .map(id => commentLike.copy(id = id))
  }

  def getLikes(commentId: CommentId): dbio.DBIO[Seq[CommentLike]] = {
    sql"""select * from "comment_likes" where "comment" = ${commentId.value}""".as[CommentLike]
  }
}
