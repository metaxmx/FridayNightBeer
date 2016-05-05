package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import services.{SessionService, UserService}
import rest.api_1_0.viewmodels.AuthenticationViewModels._
import rest.Implicits._
import util.PasswordEncoder

import scala.concurrent.Future

/**
  * Created by Christian Simon on 04.05.2016.
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
      } yield {
        userOpt match {
          case None => Ok(LoginResult(false, session._id)).withSession(fnbSessionHeaderName -> session._id)
          case Some(user) => Ok(LoginResult(true, session._id, Some(user._id))).withSession(fnbSessionHeaderName -> session._id)
        }
      }
  }

  private[this] def tryLoginUser(username: String, password: String): Future[Option[User]] = {
    userService.getUserByUsername(username) map {
      _ filter (_.password == (PasswordEncoder encodePassword password))
    }
  }

  private[this] def updateOrCreateSession(sessionOpt: Option[UserSession], userOpt: Option[User]): Future[UserSession] = {
    sessionOpt match {
      case Some(session) =>
        sessionService.updateSessionUser(session._id, userOpt) flatMap {
          case Some(updatedSession) =>
            Future.successful(updatedSession)
          case None =>
            sessionService.createSession(userOpt)
        }
      case None =>
        sessionService.insertSession(UserSession("", userOpt map { _._id }))
    }
  }

  def logout = SessionRestAction.async {
    request =>
    for {
      _ <- sessionService.removeSession(request.userSession._id)
      newSession <- sessionService.createSession(None)
    } yield {
      Ok(LogoutResult(true)).withSession(fnbSessionHeaderName -> newSession._id)
    }
  }

}
