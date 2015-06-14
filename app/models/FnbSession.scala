package models

import play.api.libs.json.Json

case class FnbSession(
  _id: String,
  user_id: Option[Int]) {

  def withUser(user_id: Option[Int]) = FnbSession(_id, user_id)

}

object FnbSession {

  implicit val format = Json.format[FnbSession]

}