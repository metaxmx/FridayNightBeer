package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class Group(
  _id: Int,
  name: String) {

  def withId(_id: Int) = Group(_id, name)

}

object Group extends BaseModel {

  implicit val bsonFormat = Macros.handler[Group]

  implicit val jsonFormat = Json.format[Group]

  def collectionName = "groups"

  implicit val groupIdReader = new BaseModelIdReader[Group, Int] {
    def getId = _._id
  }

  implicit val groupIdWriter = new BaseModelIdWriter[Group, Int] {
    def withId = _ withId _
  }

}