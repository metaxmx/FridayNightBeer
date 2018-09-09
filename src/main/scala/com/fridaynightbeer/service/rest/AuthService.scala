package com.fridaynightbeer.service.rest

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fridaynightbeer.authentication.{AuthenticatedService, AuthenticationStorage}
import com.fridaynightbeer.controller.SessionController

class AuthService(sessionController: SessionController,
                  protected val authStorage: AuthenticationStorage) extends AuthenticatedService {

  val route: Route = {
    path("authenticate") {
      get {
        extractValidSession { session =>
          complete {
            s"Session: ${session.sessionId}"
          }
        }
      }
    }
  }

}

object AuthService {

  def apply(sessionController: SessionController = SessionController(),
            authStorage: AuthenticationStorage = AuthenticationStorage.globalAuthStorage): AuthService = {
    new AuthService(sessionController, authStorage)
  }

}
