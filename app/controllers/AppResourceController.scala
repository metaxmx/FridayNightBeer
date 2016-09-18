package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import controllers.Assets.Asset
import play.api.Application
import play.api.mvc.{Action, Controller}
import util.{AppSettings, FileUploadAsset}

/**
  * Controller for dynamic resourced handled from the application directory.
  */
@Singleton
class AppResourceController @Inject() (val appSettings: AppSettings,
                                       val application: Application) extends Controller with FileUploadAsset {

  private[this] val RES_FAVICON = "favicon.ico"
  private[this] val RES_LOGO = "logo.png"

  def favicon = handleFromAppResourcesOrDefault(RES_FAVICON)

  def logo = handleFromAppResourcesOrDefault(RES_LOGO)

}
