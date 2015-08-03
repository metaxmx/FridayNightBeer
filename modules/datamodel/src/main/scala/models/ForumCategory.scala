package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros
import models.ForumPermissions.ForumPermission

case class ForumCategory(
    _id: Int,
    name: String,
    position: Int,
    forumPermissions: Option[Seq[AccessRule]]) {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Seq()).map {
    accessRule => ForumPermission(accessRule.permission) -> accessRule
  }.toMap

  def permissionGranted(permission: ForumPermission)(implicit userOpt: Option[User]): Option[Boolean] =
    forumPermissionMap.get(permission).map(_.allowed)

  def withId(_id: Int) = ForumCategory(_id, name, position, forumPermissions)

}

object ForumCategory {

  implicit val bsonFormat = Macros.handler[ForumCategory]

  implicit val jsonFormat = Json.format[ForumCategory]

  implicit val baseModel = BaseModel[ForumCategory]("categories")

  implicit val forumCategoryIdReader = new BaseModelIdReader[ForumCategory, Int] {
    def getId = _._id
  }

  implicit val forumCategoryIdWriter = new BaseModelIdWriter[ForumCategory, Int] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}