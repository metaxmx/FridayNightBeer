package models

import play.api.libs.json.Json
import models.GlobalPermissions.GlobalPermission
import models.ForumPermissions.ForumPermission

case class PermissionConfiguration(
    globalPermissions: Seq[AccessRule],
    defaultForumPermissions: Seq[AccessRule]) {

  lazy val globalPermissionMap = globalPermissions.map { accessRule => GlobalPermission(accessRule.permission) -> accessRule }.toMap

  lazy val defaultForumPermissionMap = defaultForumPermissions.map { accessRule => ForumPermission(accessRule.permission) -> accessRule }.toMap

  def globalPermissionAllowed(permission: GlobalPermission)(implicit userOpt: Option[User]): Option[Boolean] =
    globalPermissionMap.get(permission).map(_.allowed)

  def forumPermissionAllowed(permission: ForumPermission,
                             forum: Forum,
                             category: ForumCategory)(implicit userOpt: Option[User]): Option[Boolean] =
    forum.permissionGranted(permission).orElse(
      category.permissionGranted(permission).orElse(
        defaultForumPermissionMap.get(permission).map(_.allowed).orElse(None)))

}

object PermissionConfiguration {

  implicit val jsonFormat = Json.format[PermissionConfiguration]

}