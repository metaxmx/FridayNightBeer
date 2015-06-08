package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat

case class FnbSession(
  _id: BSONObjectID,
  user_id: Option[String]) {

  def withUser(user_id: Option[String]) = FnbSession(_id, user_id)

}

object FnbSession {

  implicit val format = Json.format[FnbSession]

}