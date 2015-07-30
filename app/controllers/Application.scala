package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, Controller, RequestHeader }
import Secured.parseSessionKey

import models.UserSession
import services.{ SessionService, Themes, UUIDGenerator }

@Singleton
class Application @Inject() (uuidGenerator: UUIDGenerator,
                             sessionService: SessionService) extends Controller {

  def appPage = Action.async {
    implicit request =>
      parseSessionKey.fold {
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

  def ensureSessionActive(sessionKey: String): Future[UserSession] = for {
    maybeSession <- sessionService.getSession(sessionKey)
    existingSession <- maybeSession.fold {
      sessionService.insertSession(UserSession(sessionKey, None))
    } {
      session => Future.successful(session)
    }
  } yield existingSession

  def randomUUID = Action {
    Ok(uuidGenerator.generate.toString)
  }

}

object Application {

  val JSON_TYPE = "application/json;charset=UTF-8"

}
