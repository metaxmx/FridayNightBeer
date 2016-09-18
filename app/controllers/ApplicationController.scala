package controllers

import javax.inject.{Inject, Singleton}

import controllers.SecuredController.{fnbSessionHeaderName, parseSessionKey}
import models.UserSession
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import services.{PermissionService, SessionService, SettingsService, UUIDGenerator}

import scala.concurrent.Future

@Singleton
class ApplicationController @Inject() (uuidGenerator: UUIDGenerator,
                                       sessionService: SessionService,
                                       permissionService: PermissionService,
                                       settingsService: SettingsService) extends Controller {

  def appPage = Action.async {
    implicit request =>
      val sessionKey = parseSessionKey.getOrElse(uuidGenerator.generateStr)
      for {
        _ <- ensureSessionActive(sessionKey)
        settings <- settingsService.asDto
      } yield {
        Ok(views.html.angular2App(settings)).withSession(fnbSessionHeaderName -> sessionKey)
      }
  }

  def config = Action.async {
    for {
      settings <- settingsService.asDto
    } yield {
      Ok(views.js.config(settings))
    }
  }

  def loginPage = appPage

  def registerPage = appPage

  def adminPage = appPage

  def mediaPage = appPage

  def eventsPage = appPage

  def settingsPage = appPage

  def usersPage = appPage

  def adminSystemSettingsPage = appPage

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

}
