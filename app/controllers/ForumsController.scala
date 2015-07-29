package controllers

import javax.inject.{ Inject, Singleton }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, AnyContent, Controller }
import Application.JSON_TYPE
import dto.ListForumsAggregation.createListForums
import dto.ShowForumAggregation.createShowForum
import exceptions.ApiException
import services.{ ForumCategoryService, ForumService, PostService, SessionService, ThreadService, UserService }
import play.api.libs.json.JsValue
import models.ForumCategory
import models.User
import dto.InsertCategoryDTO
import scala.concurrent.Future
import play.api.Logger

@Singleton
class ForumsController @Inject() (implicit userService: UserService,
                                  sessionsService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService,
                                  postService: PostService) extends Controller with Secured {

  def getForums = Action.async {
    withSessionOption[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          getForumsData recover {
            case e: ApiException => e.result
          }
    }
  }

  private def getForumsData(implicit userOpt: Option[User]) =
    for {
      categories <- forumCategoryService.getCategoriesForApi
      forums <- forumService.getForumsByCategoryForApi
      threads <- threadService.getThreadsByForumForApi
      userIndex <- userService.getUserIndexForApi
    } yield Ok(toJson(createListForums(categories, forums, threads, userIndex))).as(JSON_TYPE)

  def showForum(id: Int) = Action.async {
    withSessionOption[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          val dataFuture = for {
            forum <- forumService.getForumForApi(id)
            threads <- threadService.getThreadsByForumForApi
            userIndex <- userService.getUserIndexForApi
          } yield (forum, threads, userIndex)
          dataFuture map {
            case (forum, threads, userIndex) =>
              Ok(toJson(createShowForum(forum, threads, userIndex))).as(JSON_TYPE)
          } recover {
            case e: ApiException => e.result
          }
    }
  }

  def insertCategory = Action.async(parse.json) {
    withSessionOption[JsValue] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          request.body.validate[InsertCategoryDTO].fold(
            error => Future.successful(BadRequest("Bad JSON format")),
            newCategoryDTO => {
              // TODO: Check for permissions
              val dataFuture = for {
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
              dataFuture recover {
                case e: ApiException => e.result
              }
            })
    }
  }

}