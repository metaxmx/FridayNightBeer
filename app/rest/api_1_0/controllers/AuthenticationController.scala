package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import rest.Implicits._
import rest.api_1_0.viewmodels.AuthenticationViewModels._
import services.{PermissionService, SessionService, UserService}
import util.PasswordEncoder

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
        userOpt <- tryLoginUser(username, password)
        session <- updateOrCreateSession(request.maybeSession, userOpt)
      } yield {
        Ok(LoginResult(userOpt.isDefined, session._id, userOpt map (_._id))).withSession(fnbSessionHeaderName -> session._id)
      }
  }

  private[this] def tryLoginUser(username: String, password: String): Future[Option[User]] = {
    userService.getUserByUsername(username).toFuture map {
      _ filter (_.password == (PasswordEncoder encodePassword password))
    }
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
        sessionService.insertSession(UserSession("", userOpt map {
          _._id
        }))
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
