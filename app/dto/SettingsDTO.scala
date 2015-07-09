package dto

import play.api.libs.json.Json
import models.User
import models.FnbSetting
import models.FnbSetting._

case class SettingsDTO(
  siteName: String,
  anonAccess: Boolean)

object SettingsDTO {

  implicit val jsonFormat = Json.format[SettingsDTO]

  def fromSettings(settings: Seq[FnbSetting]): SettingsDTO = {
    val stringSettingsMap = settings.filter(hasStringValue).map { s => (s._id, s.value.get) }.toMap
    val longSettingsMap = settings.filter(hasLongValue).map { s => (s._id, s.numericValue.get) }.toMap
    val boolSettingsMap = settings.filter(hasBooleanValue).map { s => (s._id, s.booleanValue.get) }.toMap

    val siteName = stringSettingsMap.getOrElse(SettingSiteName, "Untitled Site")
    val anonAccess = boolSettingsMap.getOrElse(SettingAllowAnonymousAccess, false)

    SettingsDTO(siteName, anonAccess)
  }

  def hasStringValue(setting: FnbSetting): Boolean = setting.value.isDefined

  def hasLongValue(setting: FnbSetting): Boolean = setting.numericValue.isDefined

  def hasBooleanValue(setting: FnbSetting): Boolean = setting.booleanValue.isDefined

}