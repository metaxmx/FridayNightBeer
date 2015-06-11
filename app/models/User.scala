package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: String)

object User {

  implicit val format = Json.format[User]

}