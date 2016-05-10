package controllers

import controllers.ApiResults.{accessDeniedResult, invalidSessionResult}
import exceptions.{ApiException, ApiExceptions}
import models._
import permissions.AuthorizationPrincipal
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission
import play.api.mvc.{Request, RequestHeader, Result, WrappedRequest}
import services.{PermissionService, SessionService, UserService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait SecuredController extends AbstractController {

  import SecuredController.parseSessionKey

  implicit val userService: UserService

  implicit val sessionService: SessionService

  implicit val permissionService: PermissionService

  implicit def request2maybeUser(implicit request: UserOptionRequest) = request.maybeUser

  object OptionalSessionApiAction extends ApiActionRefiner[OptionalSessionRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, OptionalSessionRequest[A]]] = {
      val sessionKey = parseSessionKey(request)
      if (sessionKey.isDefined)
        (for {
          maybeSession <- sessionService.getSession(sessionKey.get).toFuture
          maybeUser <- if (maybeSession.exists(_.user.isDefined)) userService.getUser(maybeSession.get.user.get).toFuture else Future.successful(None)
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
        case Left(result)                  => Left(result)
        case Right(optionalSessionRequest) => optionalSessionRequest.maybeSessionRequest.map(Right(_)).getOrElse(Left(accessDeniedResult))
      }

  }

  object UserApiAction extends ApiActionRefiner[UserRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] =
      SessionApiAction.refine(request) map {
        case Left(result)          => Left(result)
        case Right(sessionRequest) => sessionRequest.maybeUserRequest.map(Right(_)).getOrElse(Left(accessDeniedResult))
      }

  }

  protected class UserOptPrincipal(implicit val userOpt: Option[User]) extends AuthorizationPrincipal

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def requireGlobalPermission(permission: GlobalPermission)(implicit userOpt: Option[User]): Unit = {
    val checkResult = Await.result(permissionService.checkGlobalPermission(permission), Duration.Inf)
    if (!checkResult) ApiExceptions.accessDeniedException
  }

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def requireGlobalPermissions(permissions: GlobalPermission*)(implicit userOpt: Option[User]): Unit = {
    val checkResult = Await.result(permissionService.checkGlobalPermissions(permissions: _*), Duration.Inf)
    if (!checkResult) ApiExceptions.accessDeniedException
  }

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def requireForumPermission(permission: ForumPermission, cat: ForumCategory, forum: Forum)(implicit userOpt: Option[User]): Unit = {
    implicit val principal = new UserOptPrincipal()
    val checkResult = Await.result(permissionService.checkForumPermission(cat, forum, permission), Duration.Inf)
    if (!checkResult) ApiExceptions.accessDeniedException
  }

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def requireThreadPermission(permission: ThreadPermission, cat: ForumCategory, forum: Forum, thread: Thread)(implicit userOpt: Option[User]): Unit = {
    implicit val principal = new UserOptPrincipal()
    val checkResult = Await.result(permissionService.checkThreadPermission(cat, forum, thread, permission), Duration.Inf)
    if (!checkResult) ApiExceptions.accessDeniedException
  }

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def hasForumPermission(permission: ForumPermission, cat: ForumCategory, forum: Forum)(implicit userOpt: Option[User]): Boolean = {
    implicit val principal = new UserOptPrincipal()
    val checkResult = Await.result(permissionService.checkForumPermission(cat, forum, permission), Duration.Inf)
    checkResult
  }

  @deprecated("Blocking - Will be replaced by non-blocking call in new API", "2016-05-05")
  def hasThreadPermission(permission: ThreadPermission, cat: ForumCategory, forum: Forum, thread: Thread)(implicit userOpt: Option[User]): Boolean = {
    implicit val principal = new UserOptPrincipal()
    val checkResult = Await.result(permissionService.checkThreadPermission(cat, forum, thread, permission), Duration.Inf)
    checkResult
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
