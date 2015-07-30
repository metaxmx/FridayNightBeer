package exceptions

import play.api.http.Status.{ BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, NOT_FOUND }
import play.api.libs.json.Json
import play.api.mvc.Results

import controllers.Application.JSON_TYPE
import dto.ErrorDTO

case class ApiException(code: Int, message: String, cause: Throwable) extends Exception(message, cause) {

  def this(code: Int, cause: Throwable) = this(code, null, cause)

  def this(code: Int, message: String) = this(code, message, null)

  def this(code: Int) = this(code, null, null)

  def result = Results.Status(code).apply(Json.toJson(ErrorDTO(message))).as(JSON_TYPE)

}

object ApiException {

  def appy(code: Int, cause: Throwable) = new ApiException(code, cause)

  def apply(code: Int, message: String) = new ApiException(code, message)

  def apply(code: Int) = new ApiException(code)

  def dbException = throw ApiException(INTERNAL_SERVER_ERROR, "database error")

  def notFoundException = throw ApiException(NOT_FOUND, "not found")

  def accessDeniedException = throw ApiException(FORBIDDEN, "access denied")

  def badRequestException = throw ApiException(BAD_REQUEST, "bad request")

  def invalidSessionException = throw ApiException(BAD_REQUEST, "invalid session")

}