package rest

import org.scalatestplus.play.PlaySpec
import rest.Exceptions.RestException

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

import Implicits._

/**
  * Created by Christian on 05.05.2016.
  */
class ExceptionsTest extends PlaySpec {

  "REST Exceptions" must {

    "be convertible from Exceptions" in {
      val message = "Some Dummy Message"
      val runtimeExc = new IllegalStateException(message)
      val restException = RestException(runtimeExc)

      assert (restException.isInstanceOf[RestException])

      val excBody = restException.toResult.body
      val bodyContent = Await.result(excBody.consumeData, Duration.Inf)

      assert(codec.decode(bodyContent) contains message)

    }

  }

}
