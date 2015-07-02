package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: Option[String],
  groups: Option[Seq[Int]])

object User extends BaseModel {

  implicit val bsonFormat = Macros.handler[User]

  implicit val jsonFormat = Json.format[User]

  def collectionName = "users"

}