package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class ForumCategory(
  _id: Int,
  name: String,
  position: Int,
  restriction: Option[AccessRestriction]) {

  def accessGranted(implicit userOpt: Option[User]) = restriction map { _.allowed } getOrElse true

  def withId(id: Int) = ForumCategory(_id, name, position, restriction)

}

object ForumCategory extends BaseModel {

  implicit val bsonFormat = Macros.handler[ForumCategory]

  implicit val jsonFormat = Json.format[ForumCategory]

  def collectionName = "categories"

  implicit val forumCategoryIdReader = new BaseModelIdReader[ForumCategory, Int] {
    def getId = _._id
  }

  implicit val forumCategoryIdWriter = new BaseModelIdWriter[ForumCategory, Int] {
    def withId = _ withId _
  }

}