package controllers

import javax.inject.{Inject, Singleton}

import authentication.{AuthenticatedProfile, PermissionAuthorization, UserProfile}
import models.{User, UserSession}
import play.api.http.Status._
import play.api.mvc.{Request, RequestHeader}
import services.{PermissionService, SessionService, UserService}
import util.Exceptions.{EntityAlreadyExistingException, InvalidEntityException, RestException}
import util.Implicits._
import util.{FutureOption, PasswordEncoder}
import viewmodels.AuthenticationViewModels._
import controllers.AuthenticationController._
import controllers.RestController._

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
        user <- tryLoginUser(username, password).flatten(throw LoginFailedException())
        session <- updateOrCreateSession(request.maybeSession, Some(user))
        userProfile = AuthenticatedProfile(user)
        authorization <- permissionService.createAuthorization()(userProfile)
      } yield {
        Ok(LoginResult(success = true,
          authenticationStatus(Some(session), authorization)(userProfile))).withSession(fnbSessionHeaderName -> session._id)
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
      implicit val userProfile = UserProfile()
      for {
        _ <- sessionService.removeSession(request.userSession._id)
        newSession <- sessionService.createSession(userOpt = None)
        authorization <- permissionService.createAuthorization()
      } yield {
        Ok(LogoutResult(success = true, authenticationStatus(Some(newSession), authorization))).withSession(fnbSessionHeaderName -> newSession._id)
      }
  }

  def getAuthenticationStatus = OptionalSessionRestAction {
    request =>
      Ok(GetAuthenticationStatusResult(success = true,
        authenticationStatus(request.maybeSession, request.authorization)(request.userProfile)))
  }

  private[this] def authenticationStatus(maybeSession: Option[UserSession], authorization: PermissionAuthorization)
                                        (implicit userProfile: UserProfile): AuthenticationStatus = {
    AuthenticationStatus(
      authenticated = userProfile.authenticated,
      sessionId = maybeSession.map(_._id),
      user = userProfile.userOpt map {
        user => AuthenticationStatusUser(user._id, user.username, user.email, user.displayName, user.fullName, user.avatar, user.groups)
      },
      globalPermissions = authorization.listGlobalPermissions
    )
  }

  def register = SessionRestAction.async(jsonREST[RegisterUserRequest]) {
    implicit request =>
      if (request.maybeUser.isDefined) throw InvalidEntityException("Session is already logged in as another user")
      for {
        newUserData <- validateAndParseRegistrationData(request.body)
        insertedUser <- userService.createUser(newUserData)
        session <- updateOrCreateSession(request.maybeSession, Some(insertedUser))
        userProfile = AuthenticatedProfile(insertedUser)
        authorization <- permissionService.createAuthorization()(userProfile)
      } yield {
        Ok(RegisterUserResult(success = true,
          authenticationStatus(Some(session), authorization)(userProfile))).withSession(fnbSessionHeaderName -> session._id)
      }
  }

  private[this] val emailRegexp = "[0-9a-zA-Z._+-]+@[0-9a-zA-Z._+-]+\\.[a-zA-z]+".r

  private[this] def validateAndParseRegistrationData(data: RegisterUserRequest)(implicit req: Request[_]): Future[User] = {
    if (data.password.length < 4) throw InvalidEntityException("Password too short")
    if (!emailRegexp.pattern.asPredicate().test(data.email)) throw InvalidEntityException("Invalid email address")
    val passwordEncoded = PasswordEncoder.encodePassword(data.password)
    val newUser = User("", data.username, passwordEncoded, data.username, data.email, None, None, None)
    for {
      userByUsername <- userService.getUserByUsername(data.username).toFuture
      userByEmail <- userService.getUserByEmail(data.email).toFuture
    } yield {
      if (userByUsername.isDefined) throw EntityAlreadyExistingException("Username already taken")
      if (userByEmail.isDefined) throw EntityAlreadyExistingException("Email already taken")
      newUser
    }
  }

}

object AuthenticationController {

  case class LoginFailedException()(implicit req: RequestHeader) extends RestException("Login Failed",
    statusCode = Some(FORBIDDEN), clientMessage = Some("User login failed with the provided credentials"))

}
