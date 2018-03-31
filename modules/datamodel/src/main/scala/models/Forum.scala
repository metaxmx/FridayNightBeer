package models

import authentication.PermissionAuthorization
import permissions.{ForumPermission, GlobalPermission}

case class Forum(_id: String,
                 name: String,
                 url: Option[String],
                 description: Option[String],
                 category: String,
                 position: Int,
                 readonly: Boolean,
                 forumPermissions: Option[Map[String, AccessRule]],
                 threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Forum] {

  lazy val forumPermissionMap: Map[String, AccessRule] = forumPermissions.getOrElse(Map.empty)

  lazy val threadPermissionMap: Map[String, AccessRule] = threadPermissions.getOrElse(Map.empty)

  override def withId(_id: String): Forum = copy(_id = _id)

  def checkAccess(implicit authorization: PermissionAuthorization, category: ForumCategory): Boolean =
    checkAccess(category)

  def checkAccess(category: ForumCategory)(implicit authorization: PermissionAuthorization): Boolean =
    authorization.checkForumPermissions(category, this, ForumPermission.Access) &&
      authorization.checkGlobalPermissions(GlobalPermission.Forums)
}
