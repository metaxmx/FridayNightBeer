package models

import play.api.libs.json.Json

case class Group(
  _id: Int,
  name: String)

object Group extends BaseModel {

  implicit val format = Json.format[Group]

  def collectionName = "groups"

}