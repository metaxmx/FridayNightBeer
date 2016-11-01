package controllers

import javax.inject.{Inject, Singleton}

import models.{Forum, ForumCategory}
import permissions.GlobalPermissions
import services._
import util.Exceptions._
import viewmodels.ForumAdminViewModels._
import viewmodels.GeneralViewModels.AccessRuleViewModel
import util.Implicits._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Controller for Forum and Category administration.
  */
@Singleton
class ForumAdminController @Inject() (val userService: UserService,
                                      val sessionService: SessionService,
                                      val permissionService: PermissionService,
                                      forumService: ForumService,
                                      forumCategoryService: ForumCategoryService,
                                      threadService: ThreadService) extends RestController {

  override def requiredGlobalPermission = Some(GlobalPermissions.Admin)

  def listCategoriesAndForums = UserRestAction.async {
    implicit request =>
      mapOk(retrieveCategoryAndForumsList())
  }

  def createCategory = UserRestAction.async(jsonREST[CreateForumCategoryRequest]) {
    implicit request =>
      mapOk(processCreateCategory())
  }

  def createForum(id: String) = UserRestAction.async(jsonREST[CreateForumRequest]) {
    implicit request =>
      mapOk(processCreateForum(id))
  }

  private[this] def retrieveCategoryAndForumsList(): Future[ListCategoriesResult] = {
    for {
      categories <- forumCategoryService.getCategories
      forumsByCategory <- forumService.getForumsByCategory
      threadsByForum <- threadService.getThreadsByForum
    } yield {
      ListCategoriesResult(success = true, categories.sortBy(_.position) map { cat =>
        val forums = forumsByCategory.getOrElse(cat._id, Seq.empty).sortBy(_.position)
        ListCategoriesCategory(cat._id, cat.name, AccessRuleViewModel.toViewModel(cat.forumPermissionMap),
          AccessRuleViewModel.toViewModel(cat.threadPermissionMap), forums map { forum =>
            ListCategoriesForum(forum._id, forum.name, forum.url, forum.description, forum.readonly,
              AccessRuleViewModel.toViewModel(forum.forumPermissionMap), AccessRuleViewModel.toViewModel(forum.threadPermissionMap),
              empty = threadsByForum.getOrElse(forum._id, Seq.empty).isEmpty)
          })
      })
    }
  }

  private[this] def processCreateCategory()(implicit request: UserRequest[CreateForumCategoryRequest]): Future[CreateForumCategoryResult] = {
    for {
      categories <- forumCategoryService.getCategories
      insertedCategory <- insertCategory(categories)
    } yield {
      CreateForumCategoryResult(success = true, insertedCategory._id)
    }
  }

  private[this] def insertCategory(existingCategories: Seq[ForumCategory])(implicit request: UserRequest[CreateForumCategoryRequest]): Future[ForumCategory] = {
    val data = request.body
    val nextPosition = if(existingCategories.isEmpty) 0 else existingCategories.map(_.position).max + 1
    val categoryToInsert = ForumCategory(_id = "", data.name, nextPosition,
      AccessRuleViewModel.fromViewModel(data.forumPermissions),AccessRuleViewModel.fromViewModel(data.threadPermissions))
    forumCategoryService.insertCategory(categoryToInsert)
  }

  private[this] def processCreateForum(categoryId: String)(implicit request: UserRequest[CreateForumRequest]): Future[CreateForumResult] = {
    for {
      category <- forumCategoryService.getCategory(categoryId).flatten(throw NotFoundException(s"Category not found: $categoryId"))
      forumsByCategory <- forumService.getForumsByCategory
      forumsInCategory = forumsByCategory.getOrElse(category._id, Seq.empty)
      insertedForum <- insertForum(category, forumsInCategory)
    } yield {
      CreateForumResult(success = true, insertedForum._id)
    }
  }

  private[this] def insertForum(category: ForumCategory, forumsInCategory: Seq[Forum])(implicit request: UserRequest[CreateForumRequest]): Future[Forum] = {
    val data = request.body
    val nextPosition = if(forumsInCategory.isEmpty) 0 else forumsInCategory.map(_.position).max + 1
    val forumToInsert = Forum(_id = "", data.name, data.url, data.description, category._id, nextPosition, data.readonly.contains(true),
      AccessRuleViewModel.fromViewModel(data.forumPermissions), AccessRuleViewModel.fromViewModel(data.threadPermissions))
    forumService.insertForum(forumToInsert)
  }

}
