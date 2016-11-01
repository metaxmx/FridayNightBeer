package controllers

import javax.inject.{Inject, Singleton}

import permissions.GlobalPermissions
import services._
import util.Exceptions._
import util.Joda.dateTimeOrdering
import viewmodels.ForumsViewModels._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 11.05.2016.
  */
@Singleton
class ForumsController @Inject()(val userService: UserService,
                                 val sessionService: SessionService,
                                 val permissionService: PermissionService,
                                 forumService: ForumService,
                                 forumCategoryService: ForumCategoryService,
                                 threadService: ThreadService,
                                 postService: PostService) extends RestController {

  override def requiredGlobalPermission = Some(GlobalPermissions.Forums)

  def getForums = OptionalSessionRestAction.async {
    implicit request =>
      mapOk(getForumOverview)
  }

  def showForum(url: String) = OptionalSessionRestAction.async {
    implicit request =>
      mapOk(getShowForum(url))
  }

  def showForumHead(url: String) = OptionalSessionRestAction.async {
    implicit request =>
      mapOk(getShowForumHead(url))
  }

  private[this] def getForumOverview(implicit request: OptionalSessionRequest[_]): Future[ForumOverviewResult] =
    for {
      categories <- forumCategoryService.getCategories
      forumsByCategory <- forumService.getForumsByCategory
      threadsByForum <- threadService.getThreadsByForum
      userIndex <- userService.getUserIndex
    } yield {
      ForumOverviewResult(success = true, categories.sortBy(_.position) map {
        implicit cat =>
          val catForums = forumsByCategory.getOrElse(cat._id, Seq.empty).filter(_.checkAccess).sortBy(_.position)
          ForumOverviewCategory(cat._id, cat.name, catForums map {
            implicit forum =>
              val forumThreads = threadsByForum.getOrElse(forum._id, Seq.empty).filter(_.checkAccess)
              val lastPostThread = forumThreads.sortBy(_.lastPost.date).reverse.headOption
              ForumOverviewForum(forum._id, forum.url.getOrElse(forum._id), forum.name, forum.description,
                forumThreads.length, forumThreads.map(_.posts).sum,
                for {
                  thread <- lastPostThread
                  user <- userIndex.get(thread.lastPost.user)
                } yield {
                  ForumOverviewLastPost(thread._id, thread.title, thread.lastPost.user, user.displayName, thread.lastPost.date)
                })
          })
      } filter {
        _.forums.nonEmpty
      })
    }

  private[this] def getShowForum(forumUrl: String)(implicit request: OptionalSessionRequest[_]): Future[ShowForumResult] =
    for {
      forum <- forumService.getForumByUrlOrElse(forumUrl, throw NotFoundException(s"Forum not found: $forumUrl"))
      category <- forumCategoryService.getCategoryOrElse(forum.category, throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requirePermissionCheck(forum.checkAccess(category))
      threadsForForum <- threadService.getThreadsForForum(forum._id)
      userIndex <- userService.getUserIndex
    } yield {
      val visibleThreads = threadsForForum.filter {
        _.checkAccess(category, forum)
      }.filter {
        thread =>
          userIndex.contains(thread.threadStart.user) && userIndex.contains(thread.lastPost.user)
      }.sorted
      val threadViewModels = visibleThreads map {
        thread =>
          val firstPostUser = userIndex(thread.threadStart.user)
          val firstPost = ShowForumPost(firstPostUser._id, firstPostUser.displayName, thread.threadStart.date)
          val latestPortUser = userIndex(thread.lastPost.user)
          val latestPost = ShowForumPost(latestPortUser._id, latestPortUser.displayName, thread.lastPost.date)
          ShowForumThread(thread._id, thread.url.getOrElse(thread._id), thread.title, thread.posts, thread.sticky, firstPost, latestPost)
      }
      val forumPermissions = request.authorization.listForumPermissions(category, forum)
      ShowForumResult(success = true, forum._id, forum.name, threadViewModels, forumPermissions)
    }

  private[this] def getShowForumHead(forumUrl: String)(implicit request: OptionalSessionRequest[_]): Future[ShowForumHeadResult] =
    for {
      forum <- forumService.getForumByUrlOrElse(forumUrl, throw NotFoundException(s"Forum not found: $forumUrl"))
      category <- forumCategoryService.getCategoryOrElse(forum.category, throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requirePermissionCheck(forum.checkAccess(category))
    } yield {
      val forumPermissions = request.authorization.listForumPermissions(category, forum)
      ShowForumHeadResult(success = true, forum._id, forum.url.getOrElse(forum._id), forum.name, forumPermissions)
    }
}
