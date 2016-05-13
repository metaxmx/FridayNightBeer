package models

import permissions.{Authorization, ForumPermissions, GlobalPermissions, ThreadPermissions}

case class Forum(_id: String,
                 name: String,
                 description: Option[String],
                 category: String,
                 position: Int,
                 readonly: Boolean,
                 forumPermissions: Option[Map[String, AccessRule]],
                 threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Forum] {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Map.empty)

  lazy val threadPermissionMap = threadPermissions.getOrElse(Map.empty)

  override def withId(_id: String) = copy(_id = _id)

  def checkAccess(implicit authorization: Authorization, category: ForumCategory): Boolean =
    checkAccess(category)

  def checkAccess(category: ForumCategory)(implicit authorization: Authorization) =
    authorization.checkForumPermission(category, this, ForumPermissions.Access) &&
      authorization.checkGlobalPermission(GlobalPermissions.Forums)
}
