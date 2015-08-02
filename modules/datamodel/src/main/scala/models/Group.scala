package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class Group(
    _id: String,
    name: String) {

  def withId(_id: String) = Group(_id, name)

}

object Group {

  implicit val bsonFormat = Macros.handler[Group]

  implicit val jsonFormat = Json.format[Group]

  implicit val baseModel = BaseModel[Group]("groups")

  implicit val groupIdReader = new BaseModelIdReader[Group, String] {
    def getId = _._id
  }

  implicit val forumCategoryIdWriter = new BaseModelIdWriter[Group, String] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}