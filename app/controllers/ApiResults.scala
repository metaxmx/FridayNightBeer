package controllers

import play.api.http.ContentTypes.JSON
import play.api.http.Status.{ BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, NOT_FOUND }
import play.api.libs.json.Json.{ format, toJson }
import play.api.mvc.Result
import play.api.mvc.Results.Status

@deprecated("building of new API", "2016-05-11")
case class ApiResultErrorMessage(
  error: String)

@deprecated("building of new API", "2016-05-11")
object ApiResultErrorMessage {

  implicit val jsonFormat = format[ApiResultErrorMessage]

}

@deprecated("building of new API", "2016-05-11")
object ApiResults {

  def badRequestResult = apiResult(BAD_REQUEST, "bad request")

  def invalidSessionResult = apiResult(BAD_REQUEST, "invalid session")

  def accessDeniedResult = apiResult(FORBIDDEN, "access denied")

  def notFoundResult = apiResult(NOT_FOUND, "not found")

  def dbErrorResult = apiResult(INTERNAL_SERVER_ERROR, "database error")

  def internalErrorResult = apiResult(INTERNAL_SERVER_ERROR, "internal server error")

  def apiResult(status: Int, message: String): Result =
    Status(status).apply(toJson(ApiResultErrorMessage(message))).as(JSON)
}

