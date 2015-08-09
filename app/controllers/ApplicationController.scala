package controllers

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{ Action, Controller }
import SecuredController.{ fnbSessionHeaderName, parseSessionKey }
import models.UserSession
import services.{ SessionService, Themes, UUIDGenerator }
import exceptions.ApiException
import services.PermissionService
import services.SettingsService

@Singleton
class ApplicationController @Inject() (uuidGenerator: UUIDGenerator,
                             sessionService: SessionService,
                             permissionService: PermissionService,
                             settingsService: SettingsService) extends Controller with AbstractController {

  def appPage = Action.async {
    implicit request =>
      val theme = Themes.defaultTheme
      val settings = settingsService.asDto
      parseSessionKey.fold {
        // No cookie with authkey found - generate one
        val sessionKey = uuidGenerator.generate.toString
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
