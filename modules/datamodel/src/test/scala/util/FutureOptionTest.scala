package util

import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise, TimeoutException}
import scala.concurrent.duration._

/**
  * Test for Future-Option
  */
class FutureOptionTest extends WordSpec with MustMatchers {

  val timeout: Duration = 2.seconds
  val negativeTimeout = 100.millis

  "Future-Option" should {

    "be creatable from empty" in {
      val futureOpt = FutureOption()
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe None
    }

    "be creatable from option" in {
      val strValue = "hello"
      val opt: Option[String] = Some(strValue)
      val futureOpt = FutureOption.fromOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe Some(strValue)
    }

    "be creatable from none" in {
      val opt: Option[String] = None
      val futureOpt = FutureOption.fromOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe None
    }

    "be creatable from future" in {
      val strValue = "hello"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val response = Await.result(futureOpt, timeout)

      response mustBe Some(strValue)
    }

    "be creatable from some future" in {
      val strValue = "hello"
      val opt: Option[Future[String]] = Some(Future.successful(strValue))
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe Some(strValue)
    }

    "be creatable from none future" in {
      val opt: Option[Future[String]] = None
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe None
    }

    "be creatable from some failed future" in {
      val opt: Option[Future[String]] = Some(Future.failed(new IllegalArgumentException))
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture
      intercept[IllegalArgumentException] {
        Await.result(future, timeout)
      }
    }

    "be creatable from future with some value" in {
      val strValue = "hello"
      val opt: Future[Option[String]] = Future.successful(Some(strValue))
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe Some(strValue)
    }

    "be creatable from future with none" in {
      val opt: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture
      val response = Await.result(future, timeout)

      response mustBe None
    }

    "be creatable from failed future" in {
      val opt: Future[Option[String]] = Future.failed(new IllegalArgumentException)
      val futureOpt = FutureOption(opt)
      val future = futureOpt.toFuture

      intercept[IllegalArgumentException] {
        Await.result(future, timeout)
      }
    }

    "flatten successful values" in {
      val strValue = "hello"
      val opt: Option[Future[String]] = Some(Future.successful(strValue))
      val futureOpt = FutureOption(opt)
      val future = futureOpt.flatten(throw new IllegalArgumentException)
      val response = Await.result(future, timeout)

      response mustBe strValue
    }

    "flatten missing values to default value" in {
      val strValue = "notfound"
      val opt: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(opt)
      val future = futureOpt.flatten(strValue)
      val response = Await.result(future, timeout)

      response mustBe strValue
    }

    "flatten missing values to failure" in {
      val opt: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(opt)
      val future = futureOpt.flatten(throw new IllegalArgumentException)

      intercept[IllegalArgumentException] {
        Await.result(future, timeout)
      }
    }

    "retain failure when flattening failed values" in {
      val opt: Option[Future[String]] = Some(Future.failed(new IllegalArgumentException))
      val futureOpt = FutureOption(opt)
      val future = futureOpt.flatten(throw new IllegalStateException())

      intercept[IllegalArgumentException] {
        Await.result(future, timeout)
      }
    }

    "be awaitable" in {
      val strValue = "hello"
      val opt: Option[Future[String]] = Some(Future.successful(strValue))
      val futureOpt = FutureOption(opt)
      val response = Await.result(futureOpt, timeout)

      response mustBe Some(strValue)
    }

    "delegate ready to wrapped future" in {
      val promise = Promise[String]()
      val opt: Option[Future[String]] = Some(promise.future)
      val futureOpt = FutureOption(opt)

      intercept[TimeoutException] {
        Await.ready(futureOpt, negativeTimeout)
      }
      promise.success("hello")
      Await.ready(futureOpt, timeout)
    }

    "delegate result to wrapped future" in {
      val strValue = "hello"
      val promise = Promise[String]()
      val opt: Option[Future[String]] = Some(promise.future)
      val futureOpt = FutureOption(opt)

      intercept[TimeoutException] {
        Await.result(futureOpt, negativeTimeout)
      }
      promise.success(strValue)
      val result = Await.result(futureOpt, timeout)
      result mustBe Some(strValue)
    }

    "fold successful values" in {
      val strValue = "hello"
      val expectedValue = "hellox"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val folded = futureOpt.fold(throw new IllegalArgumentException)(str => Future.successful(str + "x"))
      val response = Await.result(folded, timeout)

      response mustBe expectedValue
    }

    "fold missing values" in {
      val expectedValue = "missing"
      val fut: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(fut)
      val folded = futureOpt.fold(Future.successful(expectedValue))(str => throw new IllegalArgumentException)
      val response = Await.result(folded, timeout)

      response mustBe expectedValue
    }

    "run foreach on successful values" in {
      val strValue = "hello"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val promise = Promise[String]()
      futureOpt.foreach(str => promise.success(str))

      Await.result(promise.future, timeout) mustBe strValue
    }

    "not run foreach on missing values" in {
      val fut: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(fut)
      val promise = Promise[String]()
      futureOpt.foreach(str => promise.success(str))

      intercept[TimeoutException] {
        Await.result(promise.future, negativeTimeout)
      }
    }

    "not run foreach on failed values" in {
      val fut: Future[Option[String]] = Future.failed(new IllegalArgumentException)
      val futureOpt = FutureOption(fut)
      val promise = Promise[String]()
      futureOpt.foreach(str => promise.success(str))

      intercept[TimeoutException] {
        Await.result(promise.future, negativeTimeout)
      }
    }

    "filter successful values" in {
      val strValue = "hello"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val filtered = futureOpt.filter(_.startsWith("he"))
      val response = Await.result(filtered, timeout)

      response mustBe Some(strValue)
    }

    "filter out successful values" in {
      val strValue = "hello"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val filtered = futureOpt.filter(_.startsWith("xxx"))
      val response = Await.result(filtered, timeout)

      response mustBe None
    }

    "not filter missing values" in {
      val fut: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(fut)
      val filtered = futureOpt.filter(_.startsWith("he"))
      val response = Await.result(filtered, timeout)

      response mustBe None
    }

    "not filter failed values" in {
      val fut: Future[Option[String]] = Future.failed(new IllegalArgumentException)
      val futureOpt = FutureOption(fut)
      val filtered = futureOpt.filter(_.startsWith("he"))

      intercept[IllegalArgumentException] {
        Await.result(filtered, timeout)
      }
    }

    "map successful values" in {
      val strValue = "hello"
      val expectedValue = "hellox"
      val fut: Future[String] = Future.successful(strValue)
      val futureOpt = FutureOption.fromFuture(fut)
      val mapped = futureOpt.map(_ + "x")
      val response = Await.result(mapped, timeout)

      response mustBe Some(expectedValue)
    }

    "not map missing values" in {
      val fut: Future[Option[String]] = Future.successful(None)
      val futureOpt = FutureOption(fut)
      val mapped = futureOpt.map(_ + "x")
      val response = Await.result(mapped, timeout)

      response mustBe None
    }

    "not map failed values" in {
      val fut: Future[Option[String]] = Future.failed(new IllegalArgumentException)
      val futureOpt = FutureOption(fut)
      val mapped = futureOpt.map(_ + "x")

      intercept[IllegalArgumentException] {
        Await.result(mapped, timeout)
      }
    }

  }

}
