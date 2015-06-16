package models

import play.api.libs.json.Json

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: String,
  groups: Option[Seq[Int]])

object User {
  implicit val format = Json.format[User]
}