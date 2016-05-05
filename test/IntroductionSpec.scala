
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class IntroductionSpec extends FunSuite with Matchers with BeforeAndAfterEach {

  case class User(userId: Long, username: String, twitterName: String)
  case class Tweet(name: String, message: String)

  test("Future that returns String 'Simple Future' should be created ") {
    val future: Future[String] = ???

    Await.result(future, Inf) shouldBe "Simple Future"
  }

  test("Future that returns `Tweet` case class instance should be created ") {
    val future: Future[Tweet] = ???

    Await.result(future, Inf) shouldBe Tweet("1", "What a great conference!")
  }

  test("Future with failed execution should be created ") {
    val future: Future[Any] = ???

    Await.ready(future, Inf).onComplete {
      case Failure(e) => e shouldBe a [RuntimeException]
      case Success(_) => fail("Future should fail")
    }
  }

  test("Future tweet should be mapped to string message") {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val message: Future[String] = ???

    Await.result(message, Inf) shouldBe "Homer: Nice conference indeed!"
  }

  test("Future holding list of strings should be mapped to strings length ") {
    val tweetFuture = Future {
      List("You", "should", "map", "this", "to", "")
    }

    val tweetLengths: Future[List[Int]] = ???

    Await.result(tweetLengths, Inf) shouldBe List(3, 6, 3, 4, 2, 0)
  }

  test("Composition of multiple independent Futures") {
    val myNamePattern = Future { "This conference is called %s" }
    val name = Future { "Tieto Networking Conference" }

    val result: Future[String] = ???

    Await.result(result, Inf) shouldBe "This conference is called Tieto Networking Conference"
  }


  test("Composition of multiple dependent futures") {
    val someStringProducingFuture = Future { "Some totally random str" }
    val stringLengthCalculatingFuture = (str: String) => Future { str.length }

    val lengthOfAString: Future[Int] = ???

    Await.result(lengthOfAString, Inf) shouldBe 23
  }

  test("Future should fail if filter condition fails ") {
    val emptyTweet = Future.successful(Tweet("Homer", ""))

    val emptyTweetFutureFailed: Future[Tweet] = ???

    Await.ready(emptyTweetFutureFailed, Inf).onComplete {
      case Failure(e) => e shouldBe a [NoSuchElementException]
      case Success(_) => fail("Future should fail")
    }
  }

  test("Future should return same value if filter succeed ") {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val notEmptyTweetFuture: Future[Tweet] = ???

    Await.ready(notEmptyTweetFuture, Inf).onComplete {
      case Failure(_) => fail("Future should be successful")
      case Success(t) => t.name shouldBe "Homer"
    }
  }

  override def afterEach {
    CACHE_RETURNS_VALUE = false
  }

  /**
   * Pretend that this function is call to cache
   *
   * Returns Future[Option[User]] from user id. option is empty is user not exists in cache
   */
  var CACHE_RETURNS_VALUE = false
  def cacheCall(userId: Long): Future[Option[User]] = {
    if (CACHE_RETURNS_VALUE) Future.successful(Some(User(userId, "Spongebob","@Sponge")))
    else Future.successful(None)
  }

  /**
   * Pretend that this function is db call which returns Future of db call result
   *
   * Returns Future[User] from user id
   */
  def dbCall(userId: Long): Future[User] = Future.successful(User(userId, "Spongebob","@Sponge"))

  /**
   * Pretend that this function is some external api call that may took a while
   *
   * Return Future[List[Tweet]] from username
   */
  def apiCall(userName: String): Future[List[Tweet]] = Future {
    Thread.sleep(100)
    List(Tweet("Spongebob", "My pants are square shaped"))
  }

  test("Combine two futures with flatMap") {
    // First dbCall should be resolved and only then apiCall can be performed since it need data from dbCall

    //perform dbCall and the using its result - apiCall
    val tweetsFuture: Future[List[Tweet]] = ???

    Await.result(tweetsFuture, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  test("Same flatmap should be written using for comprehension ") {

    val eventualTweets = for {
      db <- dbCall(123)
      tweets <- apiCall(db.username)
    } yield tweets

    Await.result(eventualTweets, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  test("Flatmap with Option as a result") {
    // Try to:
    // 1. retrieve value from cache - `cacheCall`
    //   If value present:
    //      - then do `apiCall`
    //   else
    //      - retrieve value from database - `dbCall`
    //      - do apiCall
    //

    val result: Future[List[Tweet]] = ???

    // Try to write it with for comprehension

    Await.result(result, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }
 }