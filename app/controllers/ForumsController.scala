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
          val dataFuture = for {
            categories <- forumCategoryService.getCategoriesForApi
            forums <- forumService.getForumsByCategoryForApi
            threads <- threadService.getThreadsByForumForApi
            userIndex <- userService.getUserIndexForApi
          } yield (categories, forums, threads, userIndex)
          dataFuture map {
            case (categories, forums, threads, userIndex) =>
              Ok(toJson(createListForums(categories, forums, threads, userIndex))).as(JSON_TYPE)
          } recover {
            case e: ApiException => e.result
          }
    }
  }

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

}