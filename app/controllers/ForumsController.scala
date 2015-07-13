package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import org.joda.time.DateTime

import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, AnyContent, Controller }
import play.modules.reactivemongo.MongoController

import Application.JSON_TYPE
import dto.InsertedTopicDTO
import dto.ListForumsAggregation.createListForums
import dto.NewTopicDTO
import dto.ShowForumAggregation.createShowForum
import dto.ShowNewTopicDTO
import models.{ Post, Thread, ThreadPostData }
import services.{ ForumCategoryService, ForumService, PostService, SessionService, ThreadService, UserService }

@Singleton
class ForumsController @Inject() (implicit userService: UserService,
                                  sessionsService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService,
                                  postService: PostService) extends Controller with MongoController with Secured {

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
              Ok(toJson(createListForums(categories, forums, threads, userIndex))).as(JSON_TYPE)
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
                  case (threads, userIndex) => Ok(toJson(createShowForum(forum, threads, userIndex))).as(JSON_TYPE)
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
                Ok(toJson(ShowNewTopicDTO.fromForum(forum))).as(JSON_TYPE)
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
                    threadService.insertThread(threadToInsert) flatMap {
                      insertedThread =>
                        val postToInsert = Post(0, insertedThread._id, newTopicDTO.htmlContent,
                          sessionInfo.userOpt.get._id, DateTime.now, None)
                        postService.insertPost(postToInsert) map { _ => insertedThread._id }
                    } map {
                      insertedThreadId => Ok(toJson(InsertedTopicDTO(insertedThreadId))).as(JSON_TYPE)
                    }
                  }
              }
            })
    }
  }

}