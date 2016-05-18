import org.scalatest.Matchers

import scala.concurrent.Future

object Syntax extends Matchers {

  case class ExampleCaseClass(fieldOfTypeString: String)
  //When creating instance of case class no new keyword is needed
  val instanceOfCaseClass = ExampleCaseClass("Parameter which is a String")
  instanceOfCaseClass.fieldOfTypeString

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
  instanceOfSimpleClass.aMethodZWhichAddsZZZ("smth")
  SimpleClassLikeInJava.aStaticMethodZWhichAddsZZZ("smth")


  //Destructuring
  val (pairItem1, pairItem2) = ("Pair item 1 value", "Pair item 2 value")
  //Quadruple
  val (a1, b1, c1, d1) = ("a", "b", "c", "d")
  // _ means whatewa
  val (a2, _, _, d2, e2, f2 ,g2) = ("a", "b", "c", "d", "e", "f", "g")

  //Collections
  val aList = List(1, 2 ,3)
  val aVector = Vector(1, 2, 3)
  val aMap = Map("key" -> 1, "key2" -> 2)

  //Lambdas, anonymous functions, etc.
  val lambda: (String, String) => String = (a, b) => a + b

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

  //Futures
  val newFuture: Future[String] = Future("Some value")
  val newFuture2 = Future {
    println("Some complex future body")
    "That yields this string"
  }

  //You can filter future like list of single value
  newFuture2.filter(str => str.contains("smth"))
  val intFuture: Future[Int] = newFuture2.map(str => str.length)

  //Future composition
  val twoFuture = Future(2)
  val fiveFuture = Future(5)
  val tenF: Future[Int] = twoFuture.flatMap(two => fiveFuture.map(five => two * five))
  val tenF2 = twoFuture.zip(fiveFuture).map { case (one, five) => one * five }
}
