package controllers

import javax.inject.{Inject, Singleton}

import permissions.GlobalPermissions
import services._
import util.Implicits._
import viewmodels.AdminViewModels._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 18.09.2016.
  */
@Singleton
class AdministrationController @Inject() (val userService: UserService,
                                          val sessionService: SessionService,
                                          val permissionService: PermissionService,
                                          settingsService: SettingsService) extends RestController {

  override def requiredGlobalPermission = Some(GlobalPermissions.Admin)

  def getSystemSettings = UserRestAction.async {
    implicit request =>
      mapOk(getSystemSettingsViewModel)
  }

  def changeSystemSettings = UserRestAction.async(jsonREST[SystemSettingsRequest]) {
    implicit request =>
      val changedSettings = for {
        _ <- settingsService.changeSiteTitle(request.body.siteTitle)
        _ <- settingsService.changeRegisterEnabled(request.body.registerEnabled)
        changedSettings <- getSystemSettingsViewModel
      } yield changedSettings
      mapOk(changedSettings)
  }

  private[this] def getSystemSettingsViewModel(implicit request: SessionRequest[_]): Future[SystemSettingsResult] = {
    settingsService.asDto map {
      dto =>
        SystemSettingsResult(success = true, registerEnabled = dto.registerEnabled, dto.siteTitle, dto.defaultTheme)
    }
  }

}
