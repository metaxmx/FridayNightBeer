package com.fridaynightbeer.service

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class RestService {

  val route: Route = {
    pathPrefix("rest") {
      pathEndOrSingleSlash {
        complete {
          "yo."
        }
      }
    }
  }

}

object RestService {

  def apply(): RestService = new RestService()

}
