package controllers

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import dto.{ InsertCategoryDTO, ListForumsCategory, ListForumsForum, ListForumsLastPost }
import models.{ Forum, ForumCategory, ForumPermissions, Thread, User }
import services.{ ForumCategoryService, ForumService, PermissionService, PostService, SessionService, ThreadService, UserService }
import util.Joda.dateTimeOrdering
import dto.ShowForumDTO
import dto.ShowForumPost
import models.ForumPermissions.Access
import dto.ShowForumThread
import models.GlobalPermissions.Forums
import models.GlobalPermissions.Admin
import models.GlobalPermissions
import dto.ConfigureForumsForum
import dto.ConfigureForumsCategory

@Singleton
class ForumsController @Inject() (implicit val userService: UserService,
                                  val sessionService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService,
                                  postService: PostService,
                                  permissionService: PermissionService) extends Controller with SecuredController {

  def getForums = OptionalSessionApiAction.async {
    implicit request =>
      permissionService.requireGlobalPermission(Forums)
      getForumsData
  }
  
  def getConfigureForums = UserApiAction.async {
    implicit request =>
      permissionService.requireGlobalPermissions(Admin, Forums)
      getConfigureForumsData
  }

  def showForum(id: Int) = OptionalSessionApiAction.async {
    implicit request =>
      permissionService.requireGlobalPermission(Forums)
      for {
        forum <- forumService.getForumForApi(id)
        category <- forumCategoryService.getCategoryForApi(forum.category)
        threads <- threadService.getThreadsByForumForApi
        userIndex <- userService.getUserIndexForApi
      } yield Ok(toJson(createShowForum(forum, category, threads, userIndex))).as(JSON)
  }

  def insertCategory = UserApiAction.async(parse.json) {
    implicit request =>
      permissionService.requireGlobalPermissions(Admin, Forums)
      request.body.validate[InsertCategoryDTO].fold(
        error => Future.successful(BadRequest("Bad JSON format")),
        newCategoryDTO => {
          // TODO: Check for permissions
          for {
            categories <- forumCategoryService.getCategoriesForApi
            insertedCategory <- {
              // TODO: Concurrent position
              val maxCategorySequence = categories.sortBy(_.position).reverse.headOption.map(_.position).getOrElse(0)
              val categoryToInsert = ForumCategory(0, newCategoryDTO.title, maxCategorySequence + 1, None)
              forumCategoryService.insertCategory(categoryToInsert)
            }
            result <- {
              Logger.info(s"Created Category with title ${insertedCategory.name}")
              getForumsData
            }
          } yield result
        })
  }

  private def getForumsData(implicit userOpt: Option[User]) =
    for {
      categories <- forumCategoryService.getCategoriesForApi
      forums <- forumService.getForumsByCategoryForApi
      threads <- threadService.getThreadsByForumForApi
      userIndex <- userService.getUserIndexForApi
    } yield Ok(toJson(createListForums(categories, forums, threads, userIndex))).as(JSON)

  private def getConfigureForumsData(implicit userOpt: Option[User]) =
    for {
      categories <- forumCategoryService.getCategoriesForApi
      forums <- forumService.getForumsByCategoryForApi
      threads <- threadService.getThreadsByForumForApi
    } yield Ok(toJson(createConfigureForums(categories, forums, threads))).as(JSON)

  private def createListForums(allCategories: Seq[ForumCategory],
                               allForumsByCategory: Map[Int, Seq[Forum]],
                               allThreadsByForum: Map[Int, Seq[Thread]],
                               userIndex: Map[Int, User])(implicit userOpt: Option[User]): Seq[ListForumsCategory] =
    allCategories sortBy { _.position } map {
      category =>
        val forums = allForumsByCategory.getOrElse(category._id, Seq()) filter {
          permissionService.hasForumPermission(Access, _, category)
        } sortBy { _.position }
        val forumDtos = forums map {
          forum =>
            val threadsForForum = allThreadsByForum.getOrElse(forum._id, Seq()) filter { _.accessGranted }
            val lastPost = threadsForForum.sortBy { _.lastPost.date }.reverse.headOption
            val numThreads = threadsForForum.size
            val numPosts = threadsForForum.map { _.posts }.sum
            val listForumLastPost = lastPost.filter { userIndex contains _.lastPost.user } map {
              thread => ListForumsLastPost.fromThread(thread, userIndex(thread.lastPost.user))
            }
            ListForumsForum.fromForum(forum, numThreads, numPosts, listForumLastPost)
        }

        ListForumsCategory(category.name, forumDtos)
    } filter { !_.forums.isEmpty }

  private def createConfigureForums(allCategories: Seq[ForumCategory],
                                    allForumsByCategory: Map[Int, Seq[Forum]],
                                    allThreadsByForum: Map[Int, Seq[Thread]]) =
    allCategories sortBy { _.position } map {
      category =>
        val forums = allForumsByCategory.getOrElse(category._id, Seq()) sortBy { _.position }
        val forumDtos = forums map {
          forum =>
            val empty = allThreadsByForum.get(forum._id).map { _.isEmpty }.getOrElse(true)
            ConfigureForumsForum.fromForum(forum, empty)
        }

        ConfigureForumsCategory(category._id, category.name, category.position, forumDtos)
    }

  private def createShowForum(forum: Forum,
                              category: ForumCategory,
                              threads: Map[Int, Seq[Thread]],
                              userIndex: Map[Int, User])(implicit userOpt: Option[User]): ShowForumDTO = {
    val visibleThreads = threads.get(forum._id).getOrElse(Seq()).filter { _.accessGranted }
    val threadDTOs = visibleThreads.map {
      thread =>
        // TODO: Check if user exists
        val firstPostUser = userIndex(thread.threadStart.user)
        val firstPost = ShowForumPost(firstPostUser._id, firstPostUser.displayName, thread.threadStart.date)
        val latestPortUser = userIndex(thread.lastPost.user)
        val latestPost = ShowForumPost(latestPortUser._id, latestPortUser.displayName, thread.lastPost.date)
        ShowForumThread.fromThread(thread, firstPost, latestPost)
    }
    val threadDTOsSorted = threadDTOs.sortBy { _.latestPost.date }.reverse.sortWith((a, b) => a.sticky && !b.sticky)
    val permissions = permissionService.getAllowedForumPermissions(forum, category).map(_.name)
    ShowForumDTO.fromForum(forum, threadDTOsSorted, permissions)
  }

}