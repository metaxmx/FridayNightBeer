package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class FnbSetting(
  _id: String,
  value: Option[String],
  numericValue: Option[Int],
  booleanValue: Option[Boolean]) {

}

object FnbSetting extends BaseModel {

  implicit val bsonFormat = Macros.handler[FnbSetting]

  implicit val jsonFormat = Json.format[FnbSetting]

  def collectionName = "settings"

  val SettingSiteName = "SiteName"
  val SettingAllowAnonymousAccess = "AnonAccess"

}