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

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator) extends Controller with MongoController {

  def sessionCollection: JSONCollection = db.collection[JSONCollection]("sessions")

  def appPage = Action {
    implicit request =>
      request.cookies.get("fnbsession").fold {
        // No cookie with authkey found - generate one
        val sessionKey = uuidGenerator.generate.toString
        Logger.info(s"Creating new Session Key $sessionKey")
        ensureSessionActive(sessionKey)
        Ok(views.html.app(sessionKey)).withCookies(Cookie("fnbsession", sessionKey))
      } {
        cookie =>
          // Read session key from cookie
          val sessionKey = cookie.value.toString
          Logger.info(s"Found Session Key $sessionKey")
          ensureSessionActive(sessionKey)
          Ok(views.html.app(sessionKey))
      }
  }

  def ensureSessionActive(sessionKey: String) {
    Logger.info(s"Ensuring Session Key $sessionKey is loaded")
    val sessionFuture = sessionCollection.find(Json.obj("sessionkey" -> sessionKey)).one[FnbSession];
    sessionFuture.onComplete {
      result =>
        val needInsert = result.isFailure || result.get.isEmpty
        if (needInsert) {
          Logger.info(s"Inserting session for ${sessionKey}")
          sessionCollection.insert(FnbSession(BSONObjectID.generate, sessionKey, "")).onFailure {
            case error =>
              Logger.error("Error inserting session: " + error)
          }
        }
    }
  }

  def randomUUID = Action {
    Ok(uuidGenerator.generate.toString)
  }

}
