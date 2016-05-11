package services

import javax.inject.{Inject, Singleton}

import models._
import permissions.ForumPermissions.ForumPermission
import permissions.GlobalPermissions.GlobalPermission
import permissions.ThreadPermissions.ThreadPermission
import permissions._
import services.PermissionService.PermissionAuthorization
import storage.PermissionDAO
import storage.PermissionDAO.PermissionMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PermissionService @Inject()(permissionDAO: PermissionDAO) {

  def createAuthorization()(implicit userOpt: Option[User]): Future[Authorization] = {
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      new PermissionAuthorization(permissionMap)
    }
  }

}

/**
  * Companion object to [[PermissionService]].
  */
object PermissionService {

  /**
    * Closure containing the loaded permission map, so permission checks are possible once the permission map is loaded,
    * without requiring a new [[Future]] each time.
    *
    * @param permissionMap loaded permission map
    * @param userOpt       authorization principal
    */
  class PermissionAuthorization(permissionMap: PermissionMap)(implicit val userOpt: Option[User]) extends Authorization {

    override def checkGlobalPermissions(permissions: GlobalPermission*): Boolean = {
      val permissionType = GlobalPermissions.name
      permissions forall { permission =>
        val permissionName = permission.name
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
        val accessRuleChain = AccessRuleChain(globalRule.toSeq)
        accessRuleChain.allowed
      }
    }


    override def checkForumPermissions(forumCategory: ForumCategory,
                                       forum: Forum,
                                       permissions: ForumPermission*): Boolean = {
      val permissionType = ForumPermissions.name
      permissions forall { permission =>
        val permissionName = permission.name
        val globalRule = permissionMap.get(permissionType).flatMap(_.get(permissionName))
        val categoryRule = forumCategory.forumPermissionMap.get(permissionName)
        val forumRule = forum.forumPermissionMap.get(permissionName)
        val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule).flatten)
        accessRuleChain.allowed
      }
    }

    override def checkThreadPermissions(forumCategory: ForumCategory,
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
        val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule, threadRule).flatten)
        accessRuleChain.allowed
      }
    }

    override def listGlobalPermissions: Seq[String] = {
      val permissionType = GlobalPermissions.name
      GlobalPermissions.values flatMap {
        globalPermission =>
          val globalRule = permissionMap.get(permissionType).flatMap(_.get(globalPermission.name))
          val accessRuleChain = AccessRuleChain(globalRule.toSeq)
          if (accessRuleChain.allowed) Some(globalPermission.name) else None
      }
    }

    override def listForumPermissions(forumCategory: ForumCategory, forum: Forum): Seq[String] = {
      val permissionType = ForumPermissions.name
      ForumPermissions.values flatMap {
        forumPermission =>
          val globalRule = permissionMap.get(permissionType).flatMap(_.get(forumPermission.name))
          val categoryRule = forumCategory.forumPermissionMap.get(forumPermission.name)
          val forumRule = forum.forumPermissionMap.get(forumPermission.name)
          val accessRuleChain = AccessRuleChain(Seq(globalRule, categoryRule, forumRule).flatten)
          if (accessRuleChain.allowed) Some(forumPermission.name) else None
      }
    }

    override def listThreadPermissions(forumCategory: ForumCategory, forum: Forum, thread: Thread): Seq[String] = {
      val permissionType = ThreadPermissions.name
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