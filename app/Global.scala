import scala.concurrent.Future

import play.api.GlobalSettings
import play.api.mvc.{ RequestHeader, Result }

import exceptions.ApiException

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = ex match {
    case e: ApiException          => Future.successful(e.toResult)
    case ApiExceptionExtractor(e) => Future.successful(e.toResult)
    case _                        => super.onError(request, ex)
  }

  object ApiExceptionExtractor {
    def unapply(ex: Throwable): Option[ApiException] =
      if (ex.getCause != null && classOf[ApiException].isInstance(ex.getCause)) Some(ex.getCause.asInstanceOf[ApiException]) else None
  }

}
