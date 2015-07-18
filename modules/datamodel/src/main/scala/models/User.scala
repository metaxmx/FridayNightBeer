package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class User(
  _id: Int,
  username: String,
  password: String,
  displayName: String,
  fullName: Option[String],
  avatar: Option[String],
  groups: Option[Seq[Int]]) {

  def withId(_id: Int) = User(_id, username, password, displayName, fullName, avatar, groups)

}

object User extends BaseModel {

  implicit val bsonFormat = Macros.handler[User]

  implicit val jsonFormat = Json.format[User]

  def collectionName = "users"

  implicit val userIdReader = new BaseModelIdReader[User, Int] {
    def getId = _._id
  }

  implicit val userIdWriter = new BaseModelIdWriter[User, Int] {
    def withId = _ withId _
  }

}