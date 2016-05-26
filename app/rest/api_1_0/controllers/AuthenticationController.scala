package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import permissions.Authorization
import play.api.http.Status._
import play.api.mvc.RequestHeader
import rest.Exceptions.{BadRequestException, InvalidEntityException, RestException}
import rest.Implicits._
import rest.api_1_0.controllers.AuthenticationController.LoginFailedException
import rest.api_1_0.viewmodels.AuthenticationViewModels._
import services.{PermissionService, SessionService, UserService}
import util.{FutureOption, PasswordEncoder}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 04.05.2016.
  */

@Singleton
class AuthenticationController @Inject()(val userService: UserService,
                                         val sessionService: SessionService,
                                         val permissionService: PermissionService) extends RestController {

  def login = OptionalSessionRestAction.async(jsonREST[LoginRequest]) {
    implicit request =>
      val LoginRequest(username, password) = request.body
      for {
        userOpt <- tryLoginUser(username, password).flatten(throw LoginFailedException())
        session <- updateOrCreateSession(request.maybeSession, Some(userOpt))
        authorization <- permissionService.createAuthorization()(userOpt = Some(userOpt))
      } yield {
        Ok(LoginResult(success = true, authenticationStatus(Some(session), authorization))).withSession(fnbSessionHeaderName -> session._id)
      }
  }

  private[this] def tryLoginUser(username: String, password: String): FutureOption[User] = {
    userService.getUserByUsername(username) filter (_.password == (PasswordEncoder encodePassword password))
  }

  private[this] def updateOrCreateSession(sessionOpt: Option[UserSession], userOpt: Option[User]): Future[UserSession] = {
    sessionOpt match {
      case Some(session) =>
        sessionService.updateSessionUser(session._id, userOpt).toFuture flatMap {
          case Some(updatedSession) =>
            Future.successful(updatedSession)
          case None =>
            sessionService.createSession(userOpt)
        }
      case None =>
        sessionService.insertSession(UserSession("", userOpt map (_._id)))
    }
  }

  def logout = SessionRestAction.async {
    request =>
      for {
        _ <- sessionService.removeSession(request.userSession._id)
        newSession <- sessionService.createSession(userOpt = None)
        authorization <- permissionService.createAuthorization()(userOpt = None)
      } yield {
        Ok(LogoutResult(success = true, authenticationStatus(Some(newSession), authorization))).withSession(fnbSessionHeaderName -> newSession._id)
      }
  }

  def getAuthenticationStatus = OptionalSessionRestAction {
    request =>
      Ok(GetAuthenticationStatusResult(success = true, authenticationStatus(request.maybeSession, request.authorization)))
  }

  private[this] def authenticationStatus(maybeSession: Option[UserSession], authorization: Authorization): AuthenticationStatus =
    AuthenticationStatus(authorization.userOpt.isDefined, maybeSession.map(_._id), authorization.userOpt map {
      user => AuthenticationStatusUser(user._id, user.username, user.email, user.displayName, user.fullName, user.avatar, user.groups)
    }, authorization.listGlobalPermissions)

  def register = SessionRestAction.async(jsonREST[RegisterUserRequest]) {
    implicit request =>
      if (request.maybeUser.isDefined) throw InvalidEntityException("Session is already logged in as another user")
      for {
        newUserData <- validateAndParseRegistrationData(request.body)
        insertedUser <- userService.createUser(newUserData)
        session <- updateOrCreateSession(request.maybeSession, Some(insertedUser))
        authorization <- permissionService.createAuthorization()(userOpt = Some(insertedUser))
      } yield {
        Ok(RegisterUserResult(success = true, authenticationStatus(Some(session), authorization))).withSession(fnbSessionHeaderName -> session._id)
      }
  }

  private[this] def validateAndParseRegistrationData(data: RegisterUserRequest): Future[User] = {
    // TODO: Rules for Password Length
    // TODO: Validate EMail
    val passwordEncoded = PasswordEncoder.encodePassword(data.password)
    val newUser = new User("", data.username, passwordEncoded, data.username, data.email, None, None, None)
    // TODO: Checking if user with same username etc. already exists
    Future.successful(newUser)
  }

}

object AuthenticationController {

  case class LoginFailedException()(implicit req: RequestHeader) extends RestException("Login Failed",
    statusCode = Some(FORBIDDEN), clientMessage = Some("User login failed with the provided credentials"))

}
