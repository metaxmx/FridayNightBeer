package controllers

import javax.inject.Singleton

import scala.{ Left, Right }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.mvc.{ Request, RequestHeader, Result, WrappedRequest }

import ApiResults.{ accessDeniedResult, invalidSessionResult }
import exceptions.ApiException
import models.{ User, UserSession }
import services.{ SessionService, UserService }

trait SecuredController extends AbstractController {

  import SecuredController.parseSessionKey

  implicit val userService: UserService

  implicit val sessionService: SessionService

  object OptionalSessionApiAction extends ApiActionRefiner[OptionalSessionRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, OptionalSessionRequest[A]]] = {
      val sessionKey = parseSessionKey(request)
      if (sessionKey.isDefined)
        (for {
          maybeSession <- sessionService.getSessionForApi(sessionKey.get)
          maybeUser <- if (maybeSession.exists(_.user_id.isDefined)) userService.getUserForApi(maybeSession.get.user_id.get) else Future.successful(None)
        } yield {
          if (maybeSession.isDefined)
            Right(new OptionalSessionRequest(maybeSession, maybeUser, request))
          else
            Left(invalidSessionResult)
        }) recover {
          case e: ApiException => Left(e.toResult)
        }
      else
        Future.successful(Right(new OptionalSessionRequest(None, None, request)))
    }

  }

  object SessionApiAction extends ApiActionRefiner[SessionRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
      OptionalSessionApiAction.refine(request) map {
        _ match {
          case Left(result)                  => Left(result)
          case Right(optionalSessionRequest) => optionalSessionRequest.maybeSessionRequest.map(Right(_)).getOrElse(Left(accessDeniedResult))
        }
      }

  }

  object UserApiAction extends ApiActionRefiner[UserRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] =
      SessionApiAction.refine(request) map {
        _ match {
          case Left(result)          => Left(result)
          case Right(sessionRequest) => sessionRequest.maybeUserRequest.map(Right(_)).getOrElse(Left(accessDeniedResult))
        }
      }

  }

}

object SecuredController {

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
