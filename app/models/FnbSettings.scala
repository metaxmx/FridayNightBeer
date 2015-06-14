package models

import play.api.libs.json.Json

case class FnbSetting(
  _id: String,
  value: Option[String],
  numericValue: Option[Int],
  booleanValue: Option[Boolean]) {

}

object FnbSetting {

  implicit val format = Json.format[FnbSetting]
  
  val SettingSiteName = "SiteName"
  val SettingAllowAnonymousAccess = "AnonAccess"

}