package rest

import play.api.mvc.Results
import play.api.test._
import play.api.test.Helpers._
import rest.Exceptions.RestException
import org.scalatestplus.play._
import play.api.libs.json.{JsObject, JsValue}

import scala.concurrent.Future

/**
  * Created by Christian on 05.05.2016.
  */
class ExceptionsTest extends PlaySpec with Results {

  implicit val request = FakeRequest()

  "REST Exceptions" must {

    "be convertible from Exceptions" in {
      val message = "Some Dummy Message"
      val runtimeExc = new IllegalStateException(message)
      val restException = RestException(runtimeExc)

      restException mustBe a[RestException]

      val result = Future.successful(restException.toResult)

      status(result) mustBe INTERNAL_SERVER_ERROR
      contentAsString(result) must contain("unknown exception")
    }

  }

}
