package models

import play.api.libs.json.Json
import reactivemongo.bson.Macros
import models.ForumPermissions._

case class Forum(
    _id: Int,
    name: String,
    description: Option[String],
    category: Int,
    position: Int,
    readonly: Boolean,
    forumPermissions: Seq[AccessRule]) {

  lazy val forumPermissionMap: Map[ForumPermission, AccessRule] =
    forumPermissions.filter(ForumPermissions hasName _.permission).map(rule => ForumPermissions.withName(rule.permission) -> rule).toMap

  def permissionGranted(permission: ForumPermission)(implicit userOpt: Option[User]): Option[Boolean] =
    forumPermissionMap.get(Access).map(_.allowed)

  def accessGranted(implicit userOpt: Option[User]) = permissionGranted(Access).getOrElse(true)

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