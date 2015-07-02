package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class FnbSetting(
  _id: String,
  value: Option[String],
  numericValue: Option[Int],
  booleanValue: Option[Boolean]) {

}

object FnbSetting {

  implicit val bsonFormat= Macros.handler[FnbSetting]
  
  implicit val jsonFormat = Json.format[FnbSetting]
  
  val SettingSiteName = "SiteName"
  val SettingAllowAnonymousAccess = "AnonAccess"

}