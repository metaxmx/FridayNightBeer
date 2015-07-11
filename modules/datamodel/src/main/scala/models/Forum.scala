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

  def accessGranted(implicit userOpt: Option[User]) = restriction map { _.allowed } getOrElse true

  def withId(id: Int) = Forum(_id, name, description, category, position, readonly, restriction)

}

object Forum extends BaseModel {

  implicit val bsonFormat = Macros.handler[Forum]

  implicit val jsonFormat = Json.format[Forum]

  def collectionName = "forums"

  implicit val forumIdReader = new BaseModelIdReader[Forum, Int] {
    def getId = _._id
  }

  implicit val forumIdWriter = new BaseModelIdWriter[Forum, Int] {
    def withId = _ withId _
  }

}