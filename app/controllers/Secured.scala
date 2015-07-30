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
import play.api.mvc.WrappedRequest
import play.api.mvc.ActionBuilder
import play.api.mvc.ActionTransformer
import exceptions.ApiException.{ dbException, invalidSessionException, accessDeniedException }

trait Secured {

  import Secured.parseSessionKey

  implicit val userService: UserService

  implicit val sessionService: SessionService

  object OptionalSessionAction extends ActionBuilder[OptionalSessionRequest] with ActionTransformer[Request, OptionalSessionRequest] {

    override def transform[A](request: Request[A]): Future[OptionalSessionRequest[A]] =
      parseSessionKey(request).fold {
        Future.successful(new OptionalSessionRequest(None, None, request))
      } {
        sessionKey =>
          for {
            session <- sessionService.getSessionForApi(sessionKey)
            maybeUser <- session.user_id map { userId => userService.getUserForApi(userId) } getOrElse Future.successful(None)
          } yield new OptionalSessionRequest(Some(session), maybeUser, request)
      }

  }

  object SessionAction extends ActionBuilder[SessionRequest] with ActionTransformer[Request, SessionRequest] {

    override def transform[A](request: Request[A]): Future[SessionRequest[A]] =
      OptionalSessionAction.transform(request) map { _.maybeSessionRequest getOrElse accessDeniedException }

  }

  object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {

    override def transform[A](request: Request[A]) =
      SessionAction.transform(request) map { _.maybeUserRequest getOrElse accessDeniedException }

  }

}

object Secured {

  val fnbSessionHeaderName = "fnbsessionid"

  def parseSessionKey(implicit request: RequestHeader) = request.session.get(fnbSessionHeaderName)

}

trait UserOptionRequest {

  val maybeUser: Option[User]

}

class OptionalSessionRequest[A](val maybeSession: Option[UserSession],
                                val maybeUser: Option[User],
                                request: Request[A]) extends WrappedRequest[A](request) with UserOptionRequest {

  def maybeSessionRequest: Option[SessionRequest[A]] = maybeSession map {
    session => new SessionRequest[A](session, maybeUser, request)
  }

  def maybeUserRequest: Option[UserRequest[A]] = for {
    session <- maybeSession
    user <- maybeUser
  } yield new UserRequest[A](session, user, request)

}

class SessionRequest[A](val userSession: UserSession,
                        val maybeUser: Option[User],
                        request: Request[A]) extends WrappedRequest[A](request) with UserOptionRequest {

  def maybeUserRequest: Option[UserRequest[A]] = maybeUser map {
    user => new UserRequest[A](userSession, user, request)
  }

}

class UserRequest[A](val userSession: UserSession,
                     val user: User,
                     request: Request[A]) extends WrappedRequest[A](request) with UserOptionRequest {

  override val maybeUser = Some(user)

}
