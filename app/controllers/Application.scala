package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, Controller, RequestHeader }
import play.modules.reactivemongo.MongoController

import models.UserSession
import services.{ SessionService, SettingsService, Themes, UUIDGenerator }

@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator,
                             sessionService: SessionService,
                             settingsService: SettingsService) extends Controller with MongoController {

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

  def showTopicPage(id: Int) = appPage

  def ensureSessionActive(sessionKey: String): Future[UserSession] = {
    Logger.info(s"Ensuring Session Key $sessionKey is loaded")
    sessionService.getSession(sessionKey) flatMap {
      case Some(session) => {
        Future.successful(session)
      }
      case None => {
        val newSession = UserSession(sessionKey, None)
        Logger.info(s"Inserting session for ${sessionKey}")
        sessionService.insertSession(newSession)
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
