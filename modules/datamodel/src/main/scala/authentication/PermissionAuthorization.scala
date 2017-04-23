package authentication

import models.{AccessRuleChain, Forum, ForumCategory, Thread}
import permissions.ForumPermissions.ForumPermission
import permissions.{ForumPermissions, GlobalPermissions, ThreadPermissions}
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission
import storage.PermissionDAO.PermissionMap

/**
  * Authorization object containing the loaded permission map, to perform permission authorization requests
  * from the access rule authorization.
  * @param authorization access rule authorization object
  * @param permissionMap loaded permission map
  */
class PermissionAuthorization(authorization: AccessRuleAuthorization, permissionMap: PermissionMap) {

  def checkGlobalPermissions(permissions: GlobalPermission*): Boolean = {
    val permissionType = GlobalPermissions.name
    permissions forall { permission =>
      val permissionName = permission.name
      val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
      val accessRuleChain = AccessRuleChain(globalRule)
      authorization.authorize(accessRuleChain)
    }
  }


  def checkForumPermissions(forumCategory: ForumCategory,
                            forum: Forum,
                            permissions: ForumPermission*): Boolean = {
    val permissionType = ForumPermissions.name
    permissions forall { permission =>
      val permissionName = permission.name
      val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
      val categoryRule = forumCategory.forumPermissionMap.get(permissionName)
      val forumRule = forum.forumPermissionMap.get(permissionName)
      val accessRuleChain = AccessRuleChain(globalRule, categoryRule, forumRule)
      authorization.authorize(accessRuleChain)
    }
  }

  def checkThreadPermissions(forumCategory: ForumCategory,
                             forum: Forum,
                             thread: Thread,
                             permissions: ThreadPermission*): Boolean = {
    val permissionType = ThreadPermissions.name
    permissions forall { permission =>
      val permissionName = permission.name
      val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
      val categoryRule = forumCategory.threadPermissionMap.get(permissionName)
      val forumRule = forum.threadPermissionMap.get(permissionName)
      val threadRule = thread.threadPermissionMap.get(permissionName)
      val accessRuleChain = AccessRuleChain(globalRule, categoryRule, forumRule, threadRule)
      authorization.authorize(accessRuleChain)
    }
  }

  def listGlobalPermissions: Seq[String] = {
    val permissionType = GlobalPermissions.name
    GlobalPermissions.values filter {
      globalPermission =>
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(globalPermission.name))
        val accessRuleChain = AccessRuleChain(globalRule)
        authorization.authorize(accessRuleChain)
    } map {
      _.name
    }
  }

  def listForumPermissions(forumCategory: ForumCategory, forum: Forum): Seq[String] = {
    val permissionType = ForumPermissions.name
    ForumPermissions.values filter {
      forumPermission =>
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(forumPermission.name))
        val categoryRule = forumCategory.forumPermissionMap.get(forumPermission.name)
        val forumRule = forum.forumPermissionMap.get(forumPermission.name)
        val accessRuleChain = AccessRuleChain(globalRule, categoryRule, forumRule)
        authorization.authorize(accessRuleChain)
    } map {
      _.name
    }
  }

  def listThreadPermissions(forumCategory: ForumCategory, forum: Forum, thread: Thread): Seq[String] = {
    val permissionType = ThreadPermissions.name
    ThreadPermissions.values filter {
      threadPermission =>
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(threadPermission.name))
        val categoryRule = forumCategory.threadPermissionMap.get(threadPermission.name)
        val forumRule = forum.threadPermissionMap.get(threadPermission.name)
        val threadRule = thread.threadPermissionMap.get(threadPermission.name)
        val accessRuleChain = AccessRuleChain(globalRule, categoryRule, forumRule)
        authorization.authorize(accessRuleChain)
    } map {
      _.name
    }
  }

}
