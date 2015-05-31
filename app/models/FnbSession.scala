package models

import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats.BSONObjectIDFormat

case class FnbSession(
  _id: BSONObjectID,
  sessionkey: String,
  user_id: String)

object FnbSession {

  implicit val format = Json.format[FnbSession]

}