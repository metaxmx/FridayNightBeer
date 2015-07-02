package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class Group(
  _id: Int,
  name: String)

object Group extends BaseModel {

  implicit val bsonFormat= Macros.handler[Group]
  
  implicit val jsonFormat = Json.format[Group]

  def collectionName = "groups"

}