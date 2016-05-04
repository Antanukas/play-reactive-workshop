
import org.scalatest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class IntroductionSpec extends FunSuite with Matchers with BeforeAndAfterEach {

  test("Syntax") {

    case class ExampleCaseClass(fieldOfTypeString: String)
    //When creating instance of case class no new keyword is needed
    val instanceOfCaseClass = ExampleCaseClass("Parameter which is a String")
    instanceOfCaseClass.fieldOfTypeString shouldBe "Parameter which is a String"

    class SimpleClassLikeInJava(val immutableField: String, var mutableField: String) {
      def aMethodZWhichAddsZZZ(param: String): String = {
        param + "ZZZ"
      }
    }
    object SimpleClassLikeInJava { //Called Companion object. Imagine a holder of static methods
      def aStaticMethodZWhichAddsZZZ(param: String): String = {
        param + "ZZZ"
      }
    }
    val instanceOfSimpleClass = new SimpleClassLikeInJava("immutable", "mutable")
    instanceOfSimpleClass.mutableField = "new mutable value"
    //instanceOfSimpleClass.immutableField = "z" //Don't try this
    instanceOfSimpleClass.mutableField shouldBe "new mutable value"
    instanceOfSimpleClass.aMethodZWhichAddsZZZ("smth") shouldBe "smthZZZ"
    SimpleClassLikeInJava.aStaticMethodZWhichAddsZZZ("smth") shouldBe "smthZZZ"


    //Destructuring
    val (pairItem1, pairItem2) = ("Pair item 1 value", "Pair item 2 value")
    pairItem1 shouldBe "Pair item 1 value"
    pairItem2 shouldBe "Pair item 2 value"
    //Quadruple
    val (a1, b1, c1, d1) = ("a", "b", "c", "d")
    // _ means whatewa
    val (a2, _, _, d2, e2, f2 ,g2) = ("a", "b", "c", "d", "e", "f", "g")

    //Collections
    val aList = List(1, 2 ,3)
    val aVector = Vector(1, 2, 3)
    val aMap = Map("key" -> 1, "key2" -> 2)

    //Lambdas, anonymous functions, etc.
    val anonymousFunction: (String, String) => String = (a, b) => a + b
    anonymousFunction("This is ", "Tieto Networking Conference") shouldBe "This is Tieto Networking Conference"

    //Higher order functions
    List(1, 2 ,3).map(number => number * 2) shouldBe List(2, 4, 6)
    List(1, 2, 3).flatMap(number => List(number * 2)) shouldBe List(2, 4, 6)
    List(1, 2, 3).filter(number => number % 2 == 0) shouldBe List(2)
    List("a", "b", "c").map(SimpleClassLikeInJava.aStaticMethodZWhichAddsZZZ)

    //List of pairs with destructuring
    List(("Pair1Item1", "Pair1Item2"), ("Pair2Item1","Pair2Item2")).map {
      case (item1, item2) => (item2, item1)
    } shouldBe List(("Pair1Item2", "Pair1Item1"), ("Pair2Item2","Pair2Item1"))
    //Or same with pair._1, pair._2
    List(("Pair1Item1", "Pair1Item2"), ("Pair2Item1","Pair2Item2"))
      .map(pair => (pair._2, pair._1)) shouldBe List(("Pair1Item2", "Pair1Item1"), ("Pair2Item2","Pair2Item1"))

  }

  case class User(userId: Long, username: String, twitterName: String)
  case class Tweet(name: String, message: String)

  test("Future that returns String 'Simple Future' should be created ") {
    val future: Future[String] = Future {
      "Simple Future"
    }

    Await.result(future, Inf) shouldBe "Simple Future"
  }

  test("Future that returns `Tweet` case class instance should be created ") {
    val future = Future {
      Tweet("1", "What a great conference!")
    }

    Await.result(future, Inf) shouldBe Tweet("1", "What a great conference!")
  }

  test("Future with failed execution should be created ") {
    val future = Future.failed(new RuntimeException("Something bad"))
//    val future =  Future {
//      throw new RuntimeException("Error...")
//    }

    Await.ready(future, Inf).onComplete {
      case Failure(e) => e shouldBe a [RuntimeException]
      case Success(_) => fail("Future should fail")
    }
  }

  test("Future tweet should be mapped to string message") {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val message = tweet.map(t => s"${t.name}: ${t.message}")

    Await.result(message, Inf) shouldBe "Homer: Nice conference indeed!"
  }

  test("Future holding list of strings should be mapped to strings length ") {
    val tweetFuture = Future {
      List("You", "should", "map", "this", "to", "")
    }

    val tweetLengths = tweetFuture.map(tweets => tweets.map(tweet => tweet.length))

    Await.result(tweetLengths, Inf) shouldBe List(3, 6, 3, 4, 2, 0)
  }

  test("Composition of multiple independent Futures") {
    val myNamePattern = Future { "My name is %s" }
    val name = Future { "Antanas" }

    val result = myNamePattern.zip(name).map {
      case (pattern, name) => String.format(pattern, name)
    }

    Await.result(result, Inf) shouldBe "My name is Antanas"
  }


  test("Composition of multiple dependent futures") {
    val someStringProducingFuture = Future { "Some totally random str" }
    val stringLengthCalculatingFuture = (str: String) => Future { str.length }

    val lengthOfAString = someStringProducingFuture.flatMap(
      aString => stringLengthCalculatingFuture(aString))

    Await.result(lengthOfAString, Inf) shouldBe 23
  }

  test("Future should fail if filter condition fails ") {
    val emptyTweet = Future.successful(Tweet("Homer", ""))

    val emptyTweetFutureFailed = emptyTweet.filter(t => !t.message.isEmpty)

    Await.ready(emptyTweetFutureFailed, Inf).onComplete {
      case Failure(e) => e shouldBe a [NoSuchElementException]
      case Success(_) => fail("Future should fail")
    }
  }

  test("Future should return same value if filter succeed ") {
    val tweet = Future.successful(Tweet("Homer", "Nice conference indeed!"))

    val notEmptyTweetFuture = tweet.filter(t => !t.message.isEmpty)

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
    val tweetsFuture = dbCall(123)
        .flatMap(user => apiCall(user.username))

    Await.result(tweetsFuture, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  test("Same flatmap should be written using for comprehension ") {

    val eventualTweets = for {
      db <- dbCall(123)
      tweets <- apiCall(db.username)
    } yield tweets

    Await.result(eventualTweets, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }

  test("Flatmap with Option as a result") {  // TODO: maybe move to bottom
    // Try to:
    // 1. retrieve value from cache - `cacheCall`
    //   If value present:
    //      - then do `apiCall`
    //   else
    //      - retrieve value from database - `dbCall`
    //      - do apiCall
    //

    val result = cacheCall(123).flatMap { userOption =>
      if (userOption.isEmpty) {
        dbCall(123).flatMap(user => apiCall(user.username))
      } else {
        apiCall(userOption.get.username)
      }
    }

    // Try to write it with for comprehension

    Await.result(result, Inf) shouldBe List(Tweet("Spongebob", "My pants are square shaped"))
  }
 }