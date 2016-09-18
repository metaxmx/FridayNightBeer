package services

import javax.inject.{Inject, Singleton}

import org.json4s.native.Serialization.write
import storage.SystemSettingDAO
import util.JsonFormat

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SettingsService @Inject()(systemSettingDAO: SystemSettingDAO) {

  private object SettingKeys {
    val SITE_TITLE = "site_title"
    val REGISTER_ENABLED = "register_enabled"
    val DEFAULT_THEME = "default_theme"
  }

  import SettingKeys._

  def asDto: Future[SettingsDTO] = for {
    siteTitle: String <- systemSettingDAO.getSetting(SITE_TITLE, "Untitled Forum")
    registerEnabled: Boolean <- systemSettingDAO.getSetting(REGISTER_ENABLED, false)
    defaultTheme: String <- systemSettingDAO.getSetting(DEFAULT_THEME, "fnb-teal")
  } yield SettingsDTO(siteTitle, registerEnabled, defaultTheme)

  def asJson: Future[String] = asDto map (_.toJson)

  def changeSiteTitle(siteTitle: String): Future[String] = systemSettingDAO.changeSetting(SITE_TITLE, siteTitle)

  def changeRegisterEnabled(registerEnabled: Boolean): Future[Boolean] = systemSettingDAO.changeSetting(REGISTER_ENABLED, registerEnabled)

  def changeDefaultTheme(defaultTheme: String): Future[String] = systemSettingDAO.changeSetting(DEFAULT_THEME, defaultTheme)

}

case class SettingsDTO(siteTitle: String,
                       registerEnabled: Boolean,
                       defaultTheme: String) extends JsonFormat {

  def toJson: String = write(this)

}