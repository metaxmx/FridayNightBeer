package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import dto.InsertCategoryDTO
import dto.ListForumsAggregation.createListForums
import dto.ShowForumAggregation.createShowForum
import models.{ ForumCategory, User }
import services.{ ForumCategoryService, ForumService, PostService, SessionService, ThreadService, UserService }

@Singleton
class ForumsController @Inject() (implicit val userService: UserService,
                                  val sessionService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService,
                                  postService: PostService) extends Controller with SecuredController {

  def getForums = OptionalSessionApiAction.async {
    request =>
      implicit val userOpt = request.maybeUser
      getForumsData
  }

  private def getForumsData(implicit userOpt: Option[User]) =
    for {
      categories <- forumCategoryService.getCategoriesForApi
      forums <- forumService.getForumsByCategoryForApi
      threads <- threadService.getThreadsByForumForApi
      userIndex <- userService.getUserIndexForApi
    } yield Ok(toJson(createListForums(categories, forums, threads, userIndex))).as(JSON)

  def showForum(id: Int) = OptionalSessionApiAction.async {
    request =>
      implicit val userOpt = request.maybeUser
      for {
        forum <- forumService.getForumForApi(id)
        threads <- threadService.getThreadsByForumForApi
        userIndex <- userService.getUserIndexForApi
      } yield Ok(toJson(createShowForum(forum, threads, userIndex))).as(JSON)

  }

  def insertCategory = UserApiAction.async(parse.json) {
    request =>
      implicit val userOpt = request.maybeUser
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

}