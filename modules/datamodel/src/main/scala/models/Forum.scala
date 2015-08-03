package models

import play.api.libs.json.Json

import models.ForumPermissions.ForumPermission
import reactivemongo.bson.Macros

case class Forum(
    _id: Int,
    name: String,
    description: Option[String],
    category: Int,
    position: Int,
    readonly: Boolean,
    forumPermissions: Option[Seq[AccessRule]]) {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Seq()).map {
    accessRule => ForumPermission(accessRule.permission) -> accessRule
  }.toMap

  def permissionGranted(permission: ForumPermission)(implicit userOpt: Option[User]): Option[Boolean] =
    forumPermissionMap.get(permission).map(_.allowed)

  def withId(_id: Int) = Forum(_id, name, description, category, position, readonly, forumPermissions)

}

object Forum {

  implicit val bsonFormat = Macros.handler[Forum]

  implicit val jsonFormat = Json.format[Forum]

  implicit val baseModel = BaseModel[Forum]("forums")

  implicit val forumIdReader = new BaseModelIdReader[Forum, Int] {
    def getId = _._id
  }

  implicit val forumIdWriter = new BaseModelIdWriter[Forum, Int] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}