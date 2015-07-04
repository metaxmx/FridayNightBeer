package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class FnbSession(
  _id: String,
  user_id: Option[Int]) {

  def withUser(user_id: Option[Int]) = FnbSession(_id, user_id)

}

object FnbSession extends BaseModel {

  implicit val bsonFormat= Macros.handler[FnbSession]
  
  implicit val jsonFormat = Json.format[FnbSession]
  
  def collectionName = "sessions"

}