package models

import models.ForumPermissions.ForumPermission

case class ForumCategory(_id: String,
                         name: String,
                         position: Int,
                         forumPermissions: Option[Seq[AccessRule]]) extends BaseModel[ForumCategory] {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Seq()).map {
    accessRule => ForumPermission(accessRule.permission) -> accessRule
  }.toMap

  def permissionGranted(permission: ForumPermission)(implicit userOpt: Option[User]): Option[Boolean] =
    forumPermissionMap.get(permission).map(_.allowed)

  override def withId(_id: String) = copy(_id = _id)

}
