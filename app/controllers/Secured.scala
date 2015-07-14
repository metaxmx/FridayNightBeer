package controllers

import javax.inject.Singleton

import scala.annotation.implicitNotFound
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.Logger
import play.api.mvc.{ Request, RequestHeader, Result }
import play.api.mvc.Results.{ BadRequest, InternalServerError }

import models.{ User, UserSession }
import services.{ SessionService, UserService }

trait Secured {

  def parseSessionKey(request: RequestHeader) = request.session.get(Secured.fnbSessionHeaderName)

  def onMissingSession(request: RequestHeader) = {
    Logger.warn("Returning API request without session header")
    BadRequest("unauthorized")
  }

  def onSessionLoadingError[A](request: Request[A]) = {
    Logger.error("Error reading Session from DB")
    InternalServerError("session not found")
  }

  def withSessionKeyAsync[A](block: => String => Request[A] => Future[Result]): Request[A] => Future[Result] =
    request => parseSessionKey(request) match {
      case None          => Future.successful(onMissingSession(request))
      case Some(session) => block(session)(request)
    }

  def withSessionKey[A](block: => String => Request[A] => Result): Request[A] => Result =
    request => parseSessionKey(request) match {
      case None          => onMissingSession(request)
      case Some(session) => block(session)(request)
    }

  def withSession[A](block: => SessionInfo => Request[A] => Future[Result])(implicit userService: UserService, sessionService: SessionService): Request[A] => Future[Result] =
    request =>
      parseSessionKey(request) match {
        case None => Future.successful(onMissingSession(request))
        case Some(sessionKey) =>
          sessionService.getSession(sessionKey) flatMap {
            case None => Future.successful(None)
            case Some(session) => session.user_id match {
              case None => Future.successful(Some(SessionInfo(session, None)))
              case Some(userId) => userService.getUser(userId) map {
                _ map { user => SessionInfo(session, Some(user)) }
              }
            }
          } flatMap {
            case None              => Future.successful(onSessionLoadingError(request))
            case Some(sessionInfo) => block(sessionInfo)(request)
          }
      }

  def withSessionOption[A](block: => OptionalSessionInfo => Request[A] => Future[Result])(implicit userService: UserService, sessionService: SessionService): Request[A] => Future[Result] =
    request =>
      parseSessionKey(request) match {
        case None => block(OptionalSessionInfo(None, None))(request)
        case Some(sessionKey) =>
          sessionService.getSession(sessionKey) flatMap {
            case None => Future.successful(None)
            case Some(session) => session.user_id match {
              case None => Future.successful(Some(OptionalSessionInfo(Some(session), None)))
              case Some(userId) => userService.getUser(userId) map {
                _ map { user => OptionalSessionInfo(Some(session), Some(user)) }
              }
            }
          } flatMap {
            case None              => Future.successful(onSessionLoadingError(request))
            case Some(sessionInfo) => block(sessionInfo)(request)
          }
      }

}

case class SessionInfo(session: UserSession, userOpt: Option[User])

case class OptionalSessionInfo(sessionOpt: Option[UserSession], userOpt: Option[User])

object Secured {

  val fnbSessionHeaderName = "fnbsessionid"

}