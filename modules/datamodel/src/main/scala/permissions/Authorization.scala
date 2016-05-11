package permissions

import models.{Forum, ForumCategory, Thread}
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission

/**
  * Abstract authorization object.
  * Created by Christian on 10.05.2016.
  */
trait Authorization extends AuthorizationPrincipal {

  def checkGlobalPermission(permission: GlobalPermission): Boolean =
    checkGlobalPermissions(permission)

  def checkGlobalPermissions(permissions: GlobalPermission*): Boolean

  def checkForumPermission(category: ForumCategory, forum: Forum, permission: ForumPermission): Boolean =
    checkForumPermissions(category, forum, permission)

  def checkForumPermissions(category: ForumCategory, forum: Forum, permissions: ForumPermission*): Boolean

  def checkThreadPermission(category: ForumCategory, forum: Forum, thread: Thread, permission: ThreadPermission): Boolean =
    checkThreadPermissions(category, forum, thread, permission)

  def checkThreadPermissions(category: ForumCategory, forum: Forum, thread: Thread, permissions: ThreadPermission*): Boolean

  def listGlobalPermissions: Seq[String]

  def listForumPermissions(forumCategory: ForumCategory, forum: Forum): Seq[String]

  def listThreadPermissions(forumCategory: ForumCategory, forum: Forum, thread: Thread): Seq[String]

}
