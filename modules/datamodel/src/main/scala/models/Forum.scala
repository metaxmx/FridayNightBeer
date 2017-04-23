package models

import authentication.PermissionAuthorization
import permissions.{ForumPermissions, GlobalPermissions}

case class Forum(_id: String,
                 name: String,
                 url: Option[String],
                 description: Option[String],
                 category: String,
                 position: Int,
                 readonly: Boolean,
                 forumPermissions: Option[Map[String, AccessRule]],
                 threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Forum] {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Map.empty)

  lazy val threadPermissionMap = threadPermissions.getOrElse(Map.empty)

  override def withId(_id: String) = copy(_id = _id)

  def checkAccess(implicit authorization: PermissionAuthorization, category: ForumCategory): Boolean =
    checkAccess(category)

  def checkAccess(category: ForumCategory)(implicit authorization: PermissionAuthorization) =
    authorization.checkForumPermissions(category, this, ForumPermissions.Access) &&
      authorization.checkGlobalPermissions(GlobalPermissions.Forums)
}
