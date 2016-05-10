package permissions

import models.{Forum, ForumCategory, Thread}
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission

import scala.concurrent.Future

/**
  * Abstract authorization object.
  * Created by Christian on 10.05.2016.
  */
trait Authorization {

  def checkGlobalPermission(permission: GlobalPermission): Future[Boolean] =
    checkGlobalPermissions(permission)

  def checkGlobalPermissions(permissions: GlobalPermission*): Future[Boolean]

  def checkForumPermission(category: ForumCategory, forum: Forum, permission: ForumPermission): Future[Boolean] =
    checkForumPermissions(category, forum, permission)

  def checkForumPermissions(category: ForumCategory, forum: Forum, permissions: ForumPermission*): Future[Boolean]

  def checkThreadPermission(category: ForumCategory, forum: Forum, thread: Thread, permission: ThreadPermission): Future[Boolean] =
    checkThreadPermissions(category, forum, thread, permission)

  def checkThreadPermissions(category: ForumCategory, forum: Forum, thread: Thread, permissions: ThreadPermission*): Future[Boolean]

}
