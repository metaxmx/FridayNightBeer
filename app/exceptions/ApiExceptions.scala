package exceptions

import play.api.mvc.Result
import controllers.ApiResults.{ accessDeniedResult, badRequestResult, dbErrorResult, invalidSessionResult, notFoundResult }
import play.api.Logger

class ApiException(result: => Result, message: String = null, cause: Throwable = null) extends Exception(message, cause) {

  def toResult = result

}

object ApiExceptions {

  def dbException(e: Throwable) =  {
    Logger.error("Database error:", e)
    throw new ApiException(dbErrorResult)
  }

  def notFoundException = throw new ApiException(notFoundResult)

  def accessDeniedException = throw new ApiException(accessDeniedResult)

  def badRequestException = throw new ApiException(badRequestResult)

  def invalidSessionException = throw new ApiException(invalidSessionResult)

}