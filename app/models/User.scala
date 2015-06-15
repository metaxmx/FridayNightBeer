package models

import play.api.libs.json.Json

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: String)

object User {

  implicit val format = Json.format[User]

}