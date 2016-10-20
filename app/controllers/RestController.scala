package controllers

import controllers.RestController._
import models._
import permissions.Authorization
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission
import play.api.http.Writeable
import play.api.mvc._
import services.{PermissionService, SessionService, UserService}
import util.Exceptions._
import util.Implicits._
import viewmodels.ViewModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Controller trait for REST controllers.
  * Created by Christian Simon on 30.04.2016.
  */
trait RestController extends Controller {

  /** Injected User Service */
  implicit val userService: UserService

  /** Injected Session Service */
  implicit val sessionService: SessionService

  /** Injected Permission Service */
  implicit val permissionService: PermissionService

  /** Automatic extraction of [[permissions.Authorization]] from request. */
  implicit def request2authorization(implicit request: OptionalSessionRequest[_]): Authorization = request.authorization

  implicit val viewModelWritable: Writeable[ViewModel] = jsonWritable map { (vm: ViewModel) => vm.toJson }

  def requirePermissions(permissions: GlobalPermission*)(implicit request: OptionalSessionRequest[_]): Future[Unit] =
    requirePermissionCheck(request.authorization.checkGlobalPermissions(permissions: _*))

  def requireForumPermissions(category: ForumCategory, forum: Forum, permissions: ForumPermission*)(implicit request: OptionalSessionRequest[_]): Future[Unit] =
    requirePermissionCheck(request.authorization.checkForumPermissions(category, forum, permissions: _*))

  def requireThreadPermissions(category: ForumCategory, forum: Forum, thread: Thread, permissions: ThreadPermission*)(implicit request: OptionalSessionRequest[_]): Future[Unit] =
    requirePermissionCheck(request.authorization.checkThreadPermissions(category, forum, thread, permissions: _*))

  def requirePermissionCheck(checkPermission: => Boolean)(implicit req: Request[_]): Future[Unit] =
  if (checkPermission) Future.successful((): Unit) else Future.failed(ForbiddenException())

  def mapOk(viewModelFuture: Future[ViewModel]): Future[Result] = viewModelFuture map { vm => Ok(vm) }

  /**
    * Override to define a [[permissions.GlobalPermissions.GlobalPermission]] that must be checked for all Rest Actions with authorization.
    * @return [[None]] for no permission check, or [[Some]] permission to check in front of ever request
    */
  def requiredGlobalPermission: Option[GlobalPermission] = None

  /**
    * Play Action builder for REST actions with handling of [[util.Exceptions.RestException]].
    *
    * @tparam R request parameter
    */
  trait RestActionBuilder[+R[_]] extends ActionBuilder[R] {

    final def invokeBlock[A](request: Request[A], block: (R[A]) => Future[Result]) = {
      implicit val req = request
      invokeInner(request, block) recover RestException.errorHandler
    }

    /**
      * Inner action invocation.
      *
      * @param request general request
      * @param block   block to generate result
      * @tparam A request body type
      * @return result of action as [[scala.concurrent.Future]]
      */
    def invokeInner[A](request: Request[A], block: (R[A]) => Future[Result]): Future[Result]

  }

  /**
    * Play action refiner for REST actions, to refine requests to REST request classes.
    *
    * @tparam R request parameter
    */
  trait RestActionRefiner[+R[_]] extends RestActionBuilder[R] {

    final override def invokeInner[A](request: Request[A], block: R[A] => Future[Result]) =
      refine(request).flatMap(_.fold(Future.successful, block))

    /**
      * Refine request.
      *
      * @param request request to refine
      * @tparam A request body type
      * @return either refined request or result with error response
      */
    protected def refine[A](request: Request[A]): Future[Either[Result, R[A]]]

  }

  /**
    * General REST action.
    */
  object RestAction extends RestActionBuilder[Request] {

    override def invokeInner[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)

  }

  /**
    * REST action with access to parsed optional [[models.UserSession]] and [[models.User]].
    */
  object OptionalSessionRestAction extends RestActionRefiner[OptionalSessionRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, OptionalSessionRequest[A]]] =
      parseRequest(request)

  }

  /**
    * REST action with access to parsed [[models.UserSession]] and optional [[models.User]]. Requests without valid session are denied.
    */
  object SessionRestAction extends RestActionRefiner[SessionRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, SessionRequest[A]]] =
      parseRequest(request) map {
        case Left(result) => Left(result)
        case Right(req: SessionRequest[A]) => Right(req)
        case Right(other) => Left(ForbiddenException()(request).toResult)
      }

  }

  /**
    * REST action with access to parsed [[models.UserSession]] and [[models.User]]. Requests without valid session and without logged in user are denied.
    */
  object UserRestAction extends RestActionRefiner[UserRequest] {

    override def refine[A](request: Request[A]): Future[Either[Result, UserRequest[A]]] =
      parseRequest(request) map {
        case Left(result) => Left(result)
        case Right(req: UserRequest[A]) => Right(req)
        case Right(other) => Left(ForbiddenException()(request).toResult)
      }

  }

  /**
    * Parse request and return either error result or one of [[OptionalSessionRequest]], [[SessionRequest]] or [[UserRequest]].
    *
    * @param request request to parse
    * @tparam A request body type
    * @return parse result as future
    */
  protected def parseRequest[A](request: Request[A]): Future[Either[Result, OptionalSessionRequest[A]]] = {
    implicit val req = request
    def checkGlobalPermission[B <: OptionalSessionRequest[A]](r: B): Either[Result, B] = {
      requiredGlobalPermission.filterNot(p => r.authorization.checkGlobalPermission(p)) match {
        case None => Right(r) // Either no permission to check or check successful
        case Some(unmatchedPermission) => Left(ForbiddenException().toResult)
      }
    }
    parseSessionKey(request) match {
      case None =>
        permissionService.createAuthorization()(None) map {
          authorization => checkGlobalPermission(new OptionalSessionRequest[A](authorization, None, None, request))
        }
      case Some(sessionId) =>
        sessionService.getSession(sessionId).toFuture flatMap {
          case None =>
            Future.successful(Left(InvalidSessionException(sessionId).toResult))
          case Some(session) if session.user.isEmpty =>
            permissionService.createAuthorization()(None) map {
              authorization => checkGlobalPermission(new SessionRequest[A](authorization, session, None, request))
            }
          case Some(session) =>
            userService.getUser(session.user.get).toFuture flatMap {
              case None =>
                Future.successful(Left(InvalidSessionUserException(sessionId).toResult))
              case Some(user) =>
                permissionService.createAuthorization()(Some(user)) map {
                  authorization => checkGlobalPermission(new UserRequest(authorization, session, user, request))
                }
            }
        }
    }
  }

}

/**
  * Companion object to [[RestController]].
  */
object RestController {

  /** Session key to store the FNB session in. */
  protected[controllers] val fnbSessionHeaderName = "fnbsessionid"

  /**
    * Parse session key from request
    *
    * @param request target request
    * @return optional parsed session id
    */
  protected[controllers] def parseSessionKey(implicit request: RequestHeader): Option[String] = request.session.get(fnbSessionHeaderName)

}

/**
  * Wrapped Request with optional session and user.
  * @param authorization request authorization
  * @param maybeSession optional session
  * @param maybeUser    optional user
  * @param request      original request
  * @tparam A request body type
  */
class OptionalSessionRequest[A](val authorization: Authorization,
                                val maybeSession: Option[UserSession],
                                val maybeUser: Option[User],
                                request: Request[A])
  extends WrappedRequest[A](request)

/**
  * Wrapped Request with session and optional user.
  * @param authorization request authorization
  * @param userSession session
  * @param maybeUser   optional user
  * @param request     original request
  * @tparam A request body type
  */
class SessionRequest[A](authorization: Authorization,
                        val userSession: UserSession,
                        maybeUser: Option[User],
                        request: Request[A])
  extends OptionalSessionRequest(authorization, Some(userSession), maybeUser, request)

/**
  * Wrapped Request with session and user.
  *
  * @param authorization request authorization
  * @param userSession session
  * @param user        user
  * @param request     original request
  * @tparam A request body type
  */
class UserRequest[A](authorization: Authorization,
                     userSession: UserSession,
                     val user: User,
                     request: Request[A])
  extends SessionRequest(authorization, userSession, Some(user), request)