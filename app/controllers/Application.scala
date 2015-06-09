package controllers

import javax.inject.{ Singleton, Inject }
import services.UUIDGenerator
import play.api.mvc._
import play.api.Logger
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import models.FnbSession
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Await
import services.SessionsService
import scala.concurrent.Future

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator, sessionsService: SessionsService) extends Controller with MongoController {

  def appPage = Action.async {
    implicit request =>
      request.cookies.get("fnbsession").fold {
        // No cookie with authkey found - generate one
        val sessionKey = uuidGenerator.generate.toString
        Logger.info(s"Creating new Session Key $sessionKey")
        ensureSessionActive(sessionKey) map { _ =>
          Ok(views.html.app(sessionKey)).withCookies(Cookie("fnbsession", sessionKey))
        }
      } {
        cookie =>
          // Read session key from cookie
          val sessionKey = cookie.value.toString
          Logger.info(s"Found Session Key $sessionKey")
          ensureSessionActive(sessionKey) map { _ =>
            Ok(views.html.app(sessionKey))
          }
      }
  }

  def ensureSessionActive(sessionKey: String): Future[FnbSession] = {
    Logger.info(s"Ensuring Session Key $sessionKey is loaded")
    sessionsService.findSession(sessionKey) flatMap {
      case Some(session) => Future.successful(session)
      case None => {
        val newSession = FnbSession(sessionKey, None)
        Logger.info(s"Inserting session for ${sessionKey}")
        sessionsService.insertSession(newSession)
      }
    }
  }

  def randomUUID = Action {
    Ok(uuidGenerator.generate.toString)
  }

}
