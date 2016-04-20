import models.{UserId, User, Tweet}
import org.scalatest.{Matchers, FlatSpec}
import play.api.mvc.Results

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}


import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

class IntroductionSpec extends FlatSpec with Matchers {

  // TODO: consider replace FlatSpec to something more suitable

  "Future that returns String " should "be created " in {
    val future = Future {
      "Simple Future"
    }

    Await.result(future, Inf) shouldBe "Simple Future"
  }

  "Future that returns `Tweet` case class instance" should "be created " in {
    val future = Future {
      Tweet("1", "What a great conference!")
    }

    Await.result(future, Inf) shouldBe Tweet("1", "What a great conference!")
    future.failed
  }

  "Future with failed execution" should "be created " in {
    val future = Future.failed(new RuntimeException("Something bad"))

    Await.ready(future, Inf).onComplete {
      case Failure(e) => e shouldBe a [RuntimeException]
      case Success(_) => fail("Future should fail")
    }
  }

  "Future tweet" should "be mapped to string message" in {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val message = tweet.map(t => s"${t.userId}: ${t.text}")

    Await.result(message, Inf) shouldBe "Homer: Nice conference indeed!"
  }

  "Future holding list of strings" should "be mapped to strings length " in {
    val tweetFuture = Future {
      List("You", "should", "map", "this", "to", "")
    }

    val tweetLengths = tweetFuture.map(tweets => tweets.map(tweet => tweet.length))

    Await.result(tweetLengths, Inf) shouldBe List(3, 6, 3, 4, 2, 0)
  }


  "Future " should " fail if filter condition fails " in {
    val emptyTweet = Future.successful(Tweet("Homer", ""))

    val emptyTweetFutureFailed = emptyTweet.filter(t => !t.text.isEmpty)

    Await.ready(emptyTweetFutureFailed, Inf).onComplete {
      case Failure(e) => e shouldBe a [NoSuchElementException]
      case Success(_) => fail("Future should fail")
    }
  }

  "Future " should " return same value if filter succeed " in {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val notEmptyTweetFuture = tweet.filter(t => !t.text.isEmpty)

    Await.ready(notEmptyTweetFuture, Inf).onComplete {
      case Failure(_) => fail("Future should be cussesful")
      case Success(t) => t.userId shouldBe "Homer"
    }
  }

  /**
   * Pretend that this function is db call which returns Future of db call result
   */

  /** Returns Future[User] from user id */
  def dbCall(userId: UserId) = Future.successful(User(userId, "Spongebob"))

  /** Return Future[List[Tweet]] from username */
  def apiCall(userName: String) = Future(List(Tweet("Spongebob", "My pants are square shaped")))

  "Dbcall future " should " be combined with api call using flatmap" in {
    // First dbCall should be resolved and only then apiCall can be performed since it need data from dbCall

    val tweetsFuture = dbCall("123")
        .flatMap(user => apiCall(user.username))

    Await.result(tweetsFuture, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  "Same flatmap" should " be written using for comprehension " in {

    val eventualTweets = for {
      db <- dbCall("123")
      tweets <- apiCall(db.username)
    } yield tweets

    Await.result(eventualTweets, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  "Two futures " should " be zipped together" in {
    // If calls are independent from one another we can use zip function to work with two futures result
    // When zipping Future[T1] and Future[T2] zip result is Future[(T1, T2)].

    val zipped = dbCall("123").zip(dbCall("321"))

    Await.result(zipped, Inf) shouldBe (User("123", "Spongebob"), User("321", "Spongebob"))
  }

  "Two futures " should " be zipped and mapped together" in {
    // Try zip two api calls and map zipped future to Future[Int] where Int represents number of total tweets

    val tweetsCountFuture = apiCall("Homer").zip(apiCall("Spongebob"))
        .map { case (t1, t2) => t1.length + t2.length}

    Await.result(tweetsCountFuture, Inf) shouldBe 2
  }



  // andThen - with side effects

  // resilience

  // recover and recover with

  // from list of futures make future[list[t]]

}