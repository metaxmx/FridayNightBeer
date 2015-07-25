package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class Group(
  _id: String,
  name: String)

object Group {

  implicit val bsonFormat = Macros.handler[Group]

  implicit val jsonFormat = Json.format[Group]

  implicit val baseModel = BaseModel[Group]("groups")

  implicit val groupIdReader = new BaseModelIdReader[Group, String] {
    def getId = _._id
  }

}