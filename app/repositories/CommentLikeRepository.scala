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
    ???
  }

  def getLikes(commentId: CommentId): dbio.DBIO[Seq[CommentLike]] = {
    ???
  }
}
