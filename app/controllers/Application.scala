package controllers

import javax.inject.{ Singleton, Inject }
import services.UUIDGenerator
import play.api.mvc._
import play.api.Logger
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import reactivemongo.bson.BSONObjectID
import models.FnbSession
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Await
import services.SessionsService
import scala.concurrent.Future
import services.SettingsService
import services.Themes
import com.typesafe.config.{ ConfigFactory, Config }
import theme.Theme

/**
 * Instead of declaring an object of Application as per the template project, we must declare a class given that
 * the application context is going to be responsible for creating it and wiring it up with the UUID generator service.
 * @param uuidGenerator the UUID generator service we wish to receive.
 */
@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator, sessionsService: SessionsService, settingsService: SettingsService) extends Controller with MongoController {

  def parseSession(implicit req: RequestHeader) = req.session.get(Secured.fnbSessionHeaderName)

  def appPage = Action.async {
    implicit request =>
      parseSession.fold {
        // No cookie with authkey found - generate one
        val sessionKey = uuidGenerator.generate.toString
        Logger.info(s"Creating new Session Key $sessionKey")
        ensureSessionActive(sessionKey) map { _ =>
          Ok(views.html.app(Themes.defaultTheme)).withSession(Secured.fnbSessionHeaderName -> sessionKey)
        }
      } {
        sessionKey =>
          Logger.info(s"Found Session Key $sessionKey")
          ensureSessionActive(sessionKey) map { _ =>
            Ok(views.html.app(Themes.defaultTheme))
          }
      }
  }
  
  def showForumPage(id: Int) = appPage

  def showNewTopicPage(id: Int) = appPage

  def ensureSessionActive(sessionKey: String): Future[FnbSession] = {
    Logger.info(s"Ensuring Session Key $sessionKey is loaded")
    sessionsService.findSession(sessionKey) flatMap {
      case Some(session) => {
        Future.successful(session)
      }
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

  def getSettings = Action.async {
    settingsService.findSettingsDto map { dto => Ok(toJson(dto)) }
  }

}
