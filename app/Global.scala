import com.google.inject.{ Guice, AbstractModule }
import play.api.GlobalSettings
import services.{ SimpleUUIDGenerator, UUIDGenerator }
import exceptions.ApiException
import scala.concurrent.Future
import play.api.mvc.RequestHeader
import play.api.mvc.Result

/**
 * Set up the Guice injector and provide the mechanism for return objects from the dependency graph.
 */
object Global extends GlobalSettings {

  /**
   * Bind types such that whenever UUIDGenerator is required, an instance of SimpleUUIDGenerator will be used.
   */
  val injector = Guice.createInjector(new AbstractModule {
    protected def configure() {
      bind(classOf[UUIDGenerator]).to(classOf[SimpleUUIDGenerator])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  override def onError(request: RequestHeader, ex: Throwable): Future[Result] = ex match {
    case e: ApiException          => Future.successful(e.result)
    case ApiExceptionExtractor(e) => Future.successful(e.result)
    case _                        => super.onError(request, ex)
  }

  object ApiExceptionExtractor {

    def unapply(ex: Throwable): Option[ApiException] =
      if (ex.getCause != null && classOf[ApiException].isInstance(ex.getCause)) Some(ex.getCause.asInstanceOf[ApiException]) else None

  }

}
