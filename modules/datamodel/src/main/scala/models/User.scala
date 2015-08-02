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
    groups: Option[Seq[String]]) {

  def withId(_id: Int) = User(_id, username, password, displayName, fullName, avatar, groups)

}

object User {

  implicit val bsonFormat = Macros.handler[User]

  implicit val jsonFormat = Json.format[User]

  implicit val baseModel = BaseModel[User]("users")

  implicit val userIdReader = new BaseModelIdReader[User, Int] {
    def getId = _._id
  }

  implicit val userIdWriter = new BaseModelIdWriter[User, Int] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}