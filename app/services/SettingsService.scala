package services

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.libs.json.Json

@Singleton
class SettingsService @Inject()(config: Configuration) {

  lazy val defaultTheme = config.getString("fnb.defaulttheme")

  lazy val siteTitle = config.getString("fnb.sitetitle")

  lazy val registerEnabled = config.getBoolean("fnb.registerenabled")

  def asDto: SettingsDTO = SettingsDTO(
    siteTitle.getOrElse("Untitle Site"),
    registerEnabled.getOrElse(false))

  def asJson: String = Json.toJson[SettingsDTO](asDto)(SettingsDTO.jsonFormat).toString

}

case class SettingsDTO(siteTitle: String,
                       registerEnabled: Boolean) {

  def toJson = Json.toJson[SettingsDTO](this).toString

}

object SettingsDTO {

  implicit val jsonFormat = Json.format[SettingsDTO]

}