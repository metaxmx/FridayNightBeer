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

@Singleton
class ApplicationController @Inject() (uuidGenerator: UUIDGenerator,
                                       sessionService: SessionService,
                                       permissionService: PermissionService,
                                       settingsService: SettingsService,
                                       appSettings: AppSettings) extends Controller {

  def appPage = Action.async {
    implicit request =>
      val theme = Themes.defaultTheme
      val settings = settingsService.asDto
      parseSessionKey.fold {
        // No cookie with auth key found - generate one
        val sessionKey = uuidGenerator.generateStr
        ensureSessionActive(sessionKey) map { _ =>
          Ok(views.html.angular2App(theme, settings, typescriptStageMode)).withSession(fnbSessionHeaderName -> sessionKey)
        }
      } {
        sessionKey =>
          ensureSessionActive(sessionKey) map { _ =>
            Ok(views.html.angular2App(theme, settings, typescriptStageMode))
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

  /**
    * Flag, if TypeScript files were compiled into a single JS file.
    */
  lazy val typescriptStageMode: Boolean = {
    val resUrl = getClass.getClassLoader.getResource("public/main.js")
    Option(resUrl).isDefined
  }

}
