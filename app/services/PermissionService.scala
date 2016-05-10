package services

import javax.inject.{Inject, Singleton}

import models._
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission
import permissions._
import storage.PermissionDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PermissionService @Inject()(permissionDAO: PermissionDAO) {

  def checkGlobalPermission(permission: GlobalPermission)(implicit principal: AuthorizationPrincipal): Future[Boolean] =
    checkGlobalPermissions(permission)

  def checkGlobalPermissions(permissions: GlobalPermission*)(implicit principal: AuthorizationPrincipal): Future[Boolean] = {
    val permissionType = GlobalPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      permissions forall { permission =>
        val permissionName = permission.name
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
        val accessRuleChain = AccessRuleChain(globalRule.toSeq)
        accessRuleChain.allowed
      }
    }
  }

  def checkForumPermission(forumCategory: ForumCategory,
                           forum: Forum,
                           permission: ForumPermission)(implicit principal: AuthorizationPrincipal): Future[Boolean] =
    checkForumPermissions(forumCategory, forum, permission)

  def checkForumPermissions(forumCategory: ForumCategory,
                            forum: Forum,
                            permissions: ForumPermission*)(implicit principal: AuthorizationPrincipal): Future[Boolean] = {
    val permissionType = ForumPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      permissions forall { permission =>
        val permissionName = permission.name
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
        val categoryRule = forumCategory.forumPermissionMap.get(permissionName)
        val forumRule = forum.forumPermissionMap.get(permissionName)
        val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule).flatten)
        accessRuleChain.allowed
      }
    }
  }

  def checkThreadPermission(forumCategory: ForumCategory,
                            forum: Forum,
                            thread: Thread,
                            permission: ThreadPermission)(implicit principal: AuthorizationPrincipal): Future[Boolean] =
    checkThreadPermissions(forumCategory, forum, thread, permission)

  def checkThreadPermissions(forumCategory: ForumCategory,
                             forum: Forum,
                             thread: Thread,
                             permissions: ThreadPermission*)(implicit principal: AuthorizationPrincipal): Future[Boolean] = {
    val permissionType = ThreadPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      permissions forall { permission =>
        val permissionName = permission.name
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
        val categoryRule = forumCategory.threadPermissionMap.get(permissionName)
        val forumRule = forum.threadPermissionMap.get(permissionName)
        val threadRule = thread.threadPermissionMap.get(permissionName)
        val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule, threadRule).flatten)
        accessRuleChain.allowed
      }
    }
  }

  def listGlobalPermissions()(implicit principal: AuthorizationPrincipal): Future[Seq[String]] = {
    val permissionType = GlobalPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      GlobalPermissions.values flatMap {
        globalPermission =>
          val globalRule = permissionMap.get(permissionType).flatMap(_.get(globalPermission.name))
          val accessRuleChain = AccessRuleChain(globalRule.toSeq)
          if (accessRuleChain.allowed) Some(globalPermission.name) else None
      }
    }
  }

  def listForumPermissions(forumCategory: ForumCategory,
                           forum: Forum)(implicit principal: AuthorizationPrincipal): Future[Seq[String]] = {
    val permissionType = ForumPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      ForumPermissions.values flatMap {
        forumPermission =>
          val globalRule = permissionMap.get(permissionType).flatMap(_.get(forumPermission.name))
          val categoryRule = forumCategory.forumPermissionMap.get(forumPermission.name)
          val forumRule = forum.forumPermissionMap.get(forumPermission.name)
          val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule).flatten)
          if (accessRuleChain.allowed) Some(forumPermission.name) else None
      }
    }
  }

  def listThreadPermissions(forumCategory: ForumCategory,
                            forum: Forum,
                            thread: Thread)(implicit principal: AuthorizationPrincipal): Future[Seq[String]] = {
    val permissionType = ThreadPermissions.name
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      ThreadPermissions.values flatMap {
        threadPermission =>
          val globalRule = permissionMap.get(permissionType).flatMap(_.get(threadPermission.name))
          val categoryRule = forumCategory.threadPermissionMap.get(threadPermission.name)
          val forumRule = forum.threadPermissionMap.get(threadPermission.name)
          val threadRule = thread.threadPermissionMap.get(threadPermission.name)
          val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule).flatten)
          if (accessRuleChain.allowed) Some(threadPermission.name) else None
      }
    }
  }

}