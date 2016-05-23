package services

import javax.inject.{Inject, Singleton}

import org.json4s.native.Serialization.write
import play.api.Configuration
import util.JsonFormat

@Singleton
class SettingsService @Inject()(config: Configuration) {

  lazy val defaultTheme = config.getString("fnb.defaulttheme")

  lazy val siteTitle = config.getString("fnb.sitetitle")

  lazy val registerEnabled = config.getBoolean("fnb.registerenabled")

  def asDto: SettingsDTO = SettingsDTO(
    siteTitle.getOrElse("Untitle Site"),
    registerEnabled.getOrElse(false))

  def asJson: String = asDto.toJson

}

case class SettingsDTO(siteTitle: String,
                       registerEnabled: Boolean) extends JsonFormat {

  def toJson: String = write(this)

}