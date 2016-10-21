package util

import org.json4s.Extraction._
import org.json4s.MappingException
import play.api.Logger
import play.api.http.Status._
import play.api.mvc.Results.Status
import play.api.mvc.{Request, RequestHeader, Result}
import util.Implicits._
import storage.StorageException

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * REST Exceptions.
  * Created by Christian on 30.04.2016.
  */
object Exceptions {

  case class ApiErrorMessage(success: Boolean, error: String)

  /**
    * Default Rest Exception, indicating any error when calling the requested
    *
    * @param msg           error message
    * @param cause         exception cause
    * @param statusCode    status code for the error response (or None for default Ok code)
    * @param clientMessage message for the client (if diverting from error message)
    * @param reportError   flag if message should be reported with Error log level
    * @param req           request header for logging
    */
  abstract class RestException(msg: String,
                               cause: Option[Throwable] = None,
                               statusCode: Option[Int] = None,
                               clientMessage: Option[String] = None,
                               reportError: Boolean = false
                              )(implicit req: RequestHeader) extends Exception(msg, cause.orNull) {

    def toResult: Result = {
      val logFun: (=> String, => Throwable) => Unit = if (reportError) Logger.error else Logger.debug
      val logMsg = s"Request Error ${req.method} to ${req.path}: $msg"
      logFun(logMsg, cause.orNull)
      req match {
        case request: Request[_] =>
          logFun(s"Request Body: \n${request.body.toString take 100}", None.orNull)
        case _ =>
      }

      val responseEntity = decompose(ApiErrorMessage(success = false, clientMessage getOrElse msg))
      Status(statusCode getOrElse OK).apply(responseEntity)
    }

  }

  object RestException {

    def apply(exc: Throwable)(implicit req: RequestHeader): RestException = exc match {
      case re: RestException => re
      case db: StorageException => InternalDatabaseException(db.getMessage, db.getCause)
      case other => InternalServerException(other.getMessage, other)
    }

    def errorHandler()(implicit req: RequestHeader): PartialFunction[Throwable, Result] = {
      case NonFatal(exc) => RestException(exc).toResult
    }

    def errorHandlerAsync()(implicit req: RequestHeader): PartialFunction[Throwable, Future[Result]] = {
      case NonFatal(exc) => Future.successful(RestException(exc).toResult)
    }

  }

  case class InternalServerException(msg: String, cause: Throwable)(implicit req: RequestHeader) extends RestException(msg, cause = Some(cause),
    statusCode = Some(INTERNAL_SERVER_ERROR), clientMessage = Some("An unexpected error occurred during this request."), reportError = true)

  case class InternalDatabaseException(msg: String, cause: Throwable)(implicit req: RequestHeader) extends RestException(msg, cause = Some(cause),
    statusCode = Some(INTERNAL_SERVER_ERROR), clientMessage = Some("A database error occurred during this request."), reportError = true)

  case class BadRequestException(cause: Throwable)(implicit req: RequestHeader) extends RestException("Error parsing request", cause = Some(cause),
    statusCode = Some(BAD_REQUEST), clientMessage = Some("Request could not be parsed"))

  case class JsonParseException()(implicit req: RequestHeader) extends RestException("Error parsing JSON from request",
    statusCode = Some(BAD_REQUEST), clientMessage = Some("JSON from Request could not be parsed"))

  case class JsonExtractException(cause: MappingException)(implicit req: RequestHeader) extends RestException(
    "Error extracting JSON from request", cause = Some(cause),
    statusCode = Some(BAD_REQUEST), clientMessage = Some("JSON from Request could not be extracted: " + cause.msg))

  case class InvalidSessionException(sessionId: String)(implicit req: RequestHeader) extends RestException("Invalid Session",
    statusCode = Some(UNPROCESSABLE_ENTITY), clientMessage = Some(s"Invalid session id: $sessionId"))

  case class InvalidEntityException(msg: String)(implicit req: RequestHeader) extends RestException(msg,
    statusCode = Some(UNPROCESSABLE_ENTITY))

  case class InvalidSessionUserException(sessionId: String)(implicit req: RequestHeader) extends RestException(s"User for session $sessionId not found",
    statusCode = Some(INTERNAL_SERVER_ERROR), clientMessage = Some(s"Invalid user for session id: $sessionId"), reportError = true)

  case class ForbiddenException()(implicit req: RequestHeader) extends RestException("Permission not granted",
    statusCode = Some(FORBIDDEN), clientMessage = Some("You do not have the required permissions for this action"))

  case class NotFoundException(msg: String)(implicit req: RequestHeader) extends RestException(msg,
    statusCode = Some(NOT_FOUND))

  case class UnsupportedMediaTypeException(requiredType: String)(implicit req: RequestHeader) extends RestException(
    "Unsupported Media Type " + req.contentType.getOrElse("(undefined)") + s": type $requiredType required",
    statusCode = Some(UNSUPPORTED_MEDIA_TYPE), clientMessage = Some(s"Media Type $requiredType required"))

  case class EntityAlreadyExistingException(msg: String)(implicit req: RequestHeader) extends RestException(
    msg, statusCode = Some(CONFLICT))

}
