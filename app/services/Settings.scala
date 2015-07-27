package services

import com.typesafe.config.ConfigFactory

import play.api.libs.json.Json

object Settings {

  lazy val config = ConfigFactory.load()

  lazy val defaultTheme = config.getString("fnb.defaulttheme")

  lazy val siteTitle = config.getString("fnb.sitetitle")

  def asDto: SettingsDTO =
    SettingsDTO(siteTitle)

  def asJson: String = Json.toJson[SettingsDTO](asDto)(SettingsDTO.jsonFormat).toString

}

case class SettingsDTO(
  siteTitle: String)

object SettingsDTO {

  implicit val jsonFormat = Json.format[SettingsDTO]

}