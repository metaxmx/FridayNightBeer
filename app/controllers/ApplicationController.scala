package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import SecuredController.{fnbSessionHeaderName, parseSessionKey}
import models.UserSession
import services.{SessionService, Themes, UUIDGenerator}
import exceptions.ApiException
import services.PermissionService
import services.SettingsService
import util.AppSettings

@deprecated("building of new API", "2016-05-11")
@Singleton
class ApplicationController @Inject() (uuidGenerator: UUIDGenerator,
                                       sessionService: SessionService,
                                       permissionService: PermissionService,
                                       settingsService: SettingsService,
                                       appSettings: AppSettings) extends Controller with AbstractController {

  def appPage = Action.async {
    implicit request =>
      val theme = Themes.defaultTheme
      val settings = settingsService.asDto
      parseSessionKey.fold {
        // No cookie with auth key found - generate one
        val sessionKey = uuidGenerator.generateStr
        ensureSessionActive(sessionKey) map { _ =>
          Ok(views.html.app(theme, settings)).withSession(fnbSessionHeaderName -> sessionKey)
        }
      } {
        sessionKey =>
          ensureSessionActive(sessionKey) map { _ =>
            Ok(views.html.app(theme, settings))
          }
      }
  }

  def showForumPage(id: String) = appPage

  def showNewTopicPage(id: String) = appPage

  def showTopicPage(id: String) = appPage

  def ensureSessionActive(sessionKey: String): Future[UserSession] = for {
    maybeSession <- sessionService.getSession(sessionKey).toFuture
    existingSession <- maybeSession.fold {
      sessionService.insertSession(UserSession(sessionKey, None))
    } {
      session => Future.successful(session)
    }
  } yield existingSession

  def randomUUID = Action {
    Ok(uuidGenerator.generateStr)
  }

}
