package rest

import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test._
import util.Exceptions.RestException

import scala.concurrent.Future

/**
  * Created by Christian on 05.05.2016.
  */
class ExceptionsTest extends PlaySpec {

  implicit val request = FakeRequest()

  "REST Exceptions" must {

    "be convertible from Exceptions" in {
      val message = "Some Dummy Message"
      val runtimeExc = new IllegalStateException(message)
      val restException = RestException(runtimeExc)

      restException mustBe a[RestException]

      val result = Future.successful(restException.toResult)

      status(result) mustBe INTERNAL_SERVER_ERROR
    }

  }

}
