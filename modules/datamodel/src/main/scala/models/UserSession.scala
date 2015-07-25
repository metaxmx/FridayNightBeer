package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class UserSession(
  _id: String,
  user_id: Option[Int]) {

  def withUser(user_id: Option[Int]) = UserSession(_id, user_id)

}

object UserSession {

  implicit val bsonFormat = Macros.handler[UserSession]

  implicit val jsonFormat = Json.format[UserSession]

  implicit val baseModel = BaseModel[UserSession]("sessions")

  implicit val sessionIdReader = new BaseModelIdReader[UserSession, String] {
    def getId = _._id
  }

}