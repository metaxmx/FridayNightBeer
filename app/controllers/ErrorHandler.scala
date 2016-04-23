package controllers

import javax.inject.{Inject, Provider}

import exceptions.ApiException
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
                             router: Provider[Router]) extends DefaultHttpErrorHandler(env, config) {

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = exception match {
    case e: ApiException          => Future.successful(e.toResult)
    case ApiExceptionExtractor(e) => Future.successful(e.toResult)
    case _                        => super.onServerError(request, exception)
  }

  object ApiExceptionExtractor {
    def unapply(ex: Throwable): Option[ApiException] =
      if (ex.getCause != null && classOf[ApiException].isInstance(ex.getCause)) Some(ex.getCause.asInstanceOf[ApiException]) else None
  }

}
