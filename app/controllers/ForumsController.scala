package controllers

import javax.inject.{Inject, Singleton}

import scala.annotation.implicitNotFound
import scala.concurrent.Future

import org.joda.time.DateTime

import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, Controller}
import play.modules.reactivemongo.MongoController

import dto.InsertedTopicDTO
import dto.ListForumsAggregation.createListForums
import dto.NewTopicDTO
import dto.ShowForumAggregation.createShowForum
import dto.ShowNewTopicDTO
import models.{Thread, ThreadPostData}
import services.{ForumCategoryService, ForumService, SessionService, ThreadService, UserService}

@Singleton
class ForumsController @Inject() (implicit userService: UserService,
                                  sessionsService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService) extends Controller with MongoController with Secured {

  def getForums = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          val dataFuture = for {
            categories <- forumCategoryService.getCategories
            forums <- forumService.getForumsByCategory
            threads <- threadService.getThreadsByForum
            userIndex <- userService.getUserIndex
          } yield (categories, forums, threads, userIndex)
          dataFuture map {
            case (categories, forums, threads, userIndex) =>
              Ok(toJson(createListForums(categories, forums, threads, userIndex))).as("application/json")
          }
    }
  }

  def showForum(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          forumService.getForum(id) flatMap {
            case None => Future.successful(NotFound("Forum not Found"))
            case Some(forum) =>
              if (!forum.accessGranted)
                Future.successful(Forbidden("Access to forum denied"))
              else {
                val dataFuture = for {
                  threads <- threadService.getThreadsByForum
                  userIndex <- userService.getUserIndex
                } yield (threads, userIndex)
                dataFuture map {
                  case (threads, userIndex) => Ok(toJson(createShowForum(forum, threads, userIndex))).as("application/json")
                }
              }
          }
    }
  }

  def showNewTopic(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          forumService.getForum(id) map {
            case None => NotFound("Forum not Found")
            case Some(forum) =>
              if (!forum.accessGranted)
                Forbidden("Access to forum denied")
              else
                Ok(toJson(ShowNewTopicDTO.fromForum(forum))).as("application/json")
          }
    }
  }

  def newTopic(id: Int) = Action.async(parse.json) {
    withSession[JsValue] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          request.body.validate[NewTopicDTO].fold(
            error => Future.successful(BadRequest("Bad JSON format")),
            newTopicDTO => {
              forumService.getForum(id) flatMap {
                case None => Future.successful(NotFound("Forum not Found"))
                case Some(forum) =>
                  if (!forum.accessGranted)
                    Future.successful(Forbidden("Access to forum denied"))
                  else {
                    Logger info s"Create Thread with title ${newTopicDTO.title}"
                    Logger info s"HTML is: ${newTopicDTO.htmlContent}"
                    val threadToInsert = Thread(0, newTopicDTO.title, forum._id,
                      ThreadPostData(sessionInfo.userOpt.get._id, DateTime.now),
                      ThreadPostData(sessionInfo.userOpt.get._id, DateTime.now), 1, false, None)
                    threadService.insertThread(threadToInsert) map {
                      insertedThread => Ok(toJson(InsertedTopicDTO(insertedThread._id))).as("application/json")
                    }
                  }
              }
            })
    }
  }

}