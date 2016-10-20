package controllers

import javax.inject.{Inject, Provider}

import util.Exceptions.RestException
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}

import scala.concurrent._

/**
  * Handler for Server Errors.
  */
class ErrorHandler @Inject()(env: Environment,
                             config: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router]) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = exception match {
    case e: RestException          => Future.successful(e.toResult)
    case RestExceptionExtractor(e) => Future.successful(e.toResult)
    case _                         => super.onServerError(request, exception)
  }

  object RestExceptionExtractor {
    def unapply(ex: Throwable): Option[RestException] = ex.getCause match {
      case e: RestException => Some(e)
      case _ => None
    }
  }

}
