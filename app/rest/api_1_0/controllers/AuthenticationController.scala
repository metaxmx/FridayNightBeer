package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import services.{SessionService, UserService}
import rest.api_1_0.viewmodels.AuthenticationViewModels._
import rest.Implicits._
import util.PasswordEncoder

import scala.concurrent.Future

/**
  * Created by Christian on 04.05.2016.
  */

@Singleton
class AuthenticationController @Inject()(val userService: UserService,
                                         val sessionService: SessionService) extends RestController {

  def login = OptionalSessionRestAction.async(jsonREST[LoginRequest]) {
    implicit request =>
      val LoginRequest(username, password) = request.body

      for {
        userOpt <- tryLoginUser(username, password)
        session <- updateOrCreateSession(request.maybeSession, userOpt)
      }

      val passwordEncoded = PasswordEncoder encodePassword password
      userService.getUserByUsername(username) map {
        _ filter (_.password == passwordEncoded)
      } flatMap {
        userOpt =>
          request.maybeSession flatMap {
            case Some(session) => Session
          }
          sessionService.updateSessionUser()
      }
      Future.successful(Ok(LoginResult(false)))
  }

  private[this] def tryLoginUser(username: String, password: String): Future[Option[User]] = {
    userService.getUserByUsername(username) map {
      _ filter (_.password == (PasswordEncoder encodePassword password))
    }
  }

  private[this] def updateOrCreateSession(sessionOpt: Option[UserSession], userOpt: Option[User]): Future[UserSession] = {
    userOpt match {
      case Some(session) =>
        sessionService.updateSessionUser(session._id, userOpt) flatMap {
          case Some(existingSession) =>
            Future.successful(existingSession)
          case None =>
            sessionService.createSession(userOpt)
        }
      case None =>
        sessionService.insertSession(UserSession("", userOpt map { _._id }))
    }
  }

  private def loadPermissions(userOpt: Option[User]): Future

  def logout = SessionRestAction.async {
    request =>
      request.session.
  }

}
