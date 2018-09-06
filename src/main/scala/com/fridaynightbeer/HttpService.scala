package com.fridaynightbeer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.fridaynightbeer.service.{PageService, RestService}

import scala.concurrent.Future

/**
  * Http Service.
  * @param settings FNB settings
  * @param pageService page service
  * @param restService rest service
  * @param system actor system
  * @param materializer actor stream materializer
  */
class HttpService(settings: Settings,
                  pageService: PageService,
                  restService: RestService)
                 (implicit system: ActorSystem,
                  materializer: Materializer) {

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route: Route = restService.route ~ pageService.route

  private val baseRoute = Route.seal(route)

  /**
    * Start Http Server.
    *
    * @return http server binding
    */
  def runHttpService(): Future[ServerBinding] = {
    Http().bindAndHandle(baseRoute, settings.Http.interface, settings.Http.port)
  }

}

object HttpService {

  def apply(settings: Settings = FridayNightBeer.settings,
            pageService: PageService = PageService(),
            restService: RestService = RestService())
           (implicit system: ActorSystem = FridayNightBeer.system,
            materializer: Materializer = FridayNightBeer.materializer): HttpService = {
    new HttpService(settings, pageService, restService)
  }

}
