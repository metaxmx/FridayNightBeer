package com.fridaynightbeer.service

import akka.http.scaladsl.model.ContentTypes.`text/html(UTF-8)`
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.io.Codec.UTF8
import scala.io.Source

class PageService {

  private val index = Source.fromResource("webapp/index.html")(UTF8).mkString

  val route: Route = {
    pathEndOrSingleSlash {
      get {
        complete {
          HttpEntity(`text/html(UTF-8)`, index)
        }
      }
    }
  }

}

object PageService {

  def apply(): PageService = new PageService()
  
}
