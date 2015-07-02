package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class Forum(
  _id: Int,
  name: String,
  description: Option[String],
  category: Int,
  position: Int,
  readonly: Boolean,
  restriction: Option[AccessRestriction]) {

  def accessGranted(userOpt: Option[User]) = restriction.isEmpty || restriction.get.allowed(userOpt)

}

object Forum extends BaseModel {

  implicit val bsonFormat= Macros.handler[Forum]
  
  implicit val jsonFormat = Json.format[Forum]

  def collectionName = "forums"

}