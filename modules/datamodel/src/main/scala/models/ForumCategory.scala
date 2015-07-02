package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class ForumCategory(
  _id: Int,
  name: String,
  position: Int,
  restriction: Option[AccessRestriction]) {

  def accessGranted(userOpt: Option[User]) = restriction.map { _.allowed(userOpt) }.getOrElse(true)

}

object ForumCategory extends BaseModel {

  implicit val bsonFormat= Macros.handler[ForumCategory]
  
  implicit val jsonFormat = Json.format[ForumCategory]

  def collectionName = "categories"

}