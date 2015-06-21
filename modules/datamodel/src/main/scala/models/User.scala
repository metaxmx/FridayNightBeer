package models

import play.api.libs.json.Json

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: Option[String],
  groups: Option[Seq[Int]])

object User extends BaseModel {
  
  implicit val format = Json.format[User]
  
  def collectionName = "users"
  
}