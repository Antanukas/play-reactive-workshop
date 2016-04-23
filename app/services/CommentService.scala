package services

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import models._
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

class CommentService @Inject() (eventPublisher: EventPublisher)(implicit exec: ExecutionContext) {

  val commentCount  = new AtomicInteger(5)
  var comments = Seq(
    Comment(id = 0, userId = UserId("1"), username="Vasia", gitHubId = GitHubRepositoryId("4"), text = "Some nice message", createdOn = DateTime.now()),
    Comment(id = 1, userId = UserId("2"), username="Vadimka", gitHubId = GitHubRepositoryId("4"), text = "Some message", createdOn = DateTime.now()),
    Comment(id = 2, userId = UserId("3"), username="Tajana", gitHubId = GitHubRepositoryId("4"), text = "OMG", createdOn = DateTime.now()))

  val OldestToNewestOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  //TODO fetch from repo
  def getRepositoryComments(repoId: GitHubRepositoryId): Future[Seq[Comment]] = {
    Future(comments.sortBy(_.createdOn)(OldestToNewestOrdering.reverse))
  }

  def getRepositoryCommentsSource(repoId: GitHubRepositoryId): Source[Seq[Comment], NotUsed] = eventPublisher.subscribe
    .filter { case event: NewCommentEvent => event.gitHubId == repoId }
    .mapAsync(1) { _ => getRepositoryComments(repoId) }

  def create(repoId: GitHubRepositoryId, newComment: NewComment): Future[Comment] = {

    val comment = Comment(
      id = commentCount.incrementAndGet(),
      userId = newComment.userId,
      username = "Test" + newComment.userId,
      gitHubId = newComment.gitHubId,
      text = newComment.text,
      createdOn = DateTime.now())
    comments = comments :+ comment //TODO persist
    eventPublisher.publish(NewCommentEvent(repoId, comment.id))
    Future (comment)
  }
}
