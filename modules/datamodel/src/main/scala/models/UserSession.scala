package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class UserSession(
    _id: String,
    user: Option[Int]) {

  def withUser(user_id: Option[Int]) = UserSession(_id, user)

  def withId(_id: String) = UserSession(_id, user)

}

object UserSession {

  implicit val bsonFormat = Macros.handler[UserSession]

  implicit val jsonFormat = Json.format[UserSession]

  implicit val baseModel = BaseModel[UserSession]("sessions")

  implicit val sessionIdReader = new BaseModelIdReader[UserSession, String] {
    def getId = _._id
  }

  implicit val sessionIdWriter = new BaseModelIdWriter[UserSession, String] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}