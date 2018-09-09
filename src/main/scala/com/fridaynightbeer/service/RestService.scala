package com.fridaynightbeer.service

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.fridaynightbeer.authentication.AuthenticatedService
import com.fridaynightbeer.service.rest.AuthService

class RestService(authService: AuthService) {

  val route: Route = {
    pathPrefix("rest") {
      authService.route
    }
  }

}

object RestService {

  def apply(authService: AuthService = AuthService()): RestService = new RestService(authService)
  
}
