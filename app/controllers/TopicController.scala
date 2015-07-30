package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.joda.time.DateTime

import play.api.Logger
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, AnyContent, Controller }

import Application.JSON_TYPE
import dto.{ InsertTopicDTO, InsertTopicRequestDTO, InsertTopicResultDTO }
import dto.ShowThreadAggregation.createShowThread
import exceptions.ApiException
import models.{ Post, Thread, ThreadPostData }
import services.{ ForumService, PostService, SessionService, ThreadService, UserService }

@Singleton
class TopicController @Inject() (implicit val userService: UserService,
                                 val sessionService: SessionService,
                                 forumService: ForumService,
                                 threadService: ThreadService,
                                 postService: PostService) extends Controller with Secured {

  def showNewTopic(id: Int) = UserAction.async {
    request =>
      implicit val userOpt = request.maybeUser
      for {
        forum <- forumService.getForumForApi(id)
      } yield Ok(toJson(InsertTopicRequestDTO.fromForum(forum))).as(JSON_TYPE)

  }

  def newTopic(id: Int) = UserAction.async(parse.json) {
    request =>
      implicit val userOpt = request.maybeUser
      request.body.validate[InsertTopicDTO].fold(
        error => Future.successful(BadRequest("Bad JSON format")),
        newTopicDTO => {
          for {
            forum <- forumService.getForumForApi(id)
            insertedThread <- {
              val threadToInsert = Thread(0, newTopicDTO.title, forum._id,
                ThreadPostData(userOpt.get._id, DateTime.now),
                ThreadPostData(userOpt.get._id, DateTime.now), 1, false, None)
              threadService insertThread threadToInsert
            }
            insertedPost <- {
              val postToInsert = Post(0, insertedThread._id, newTopicDTO.htmlContent,
                userOpt.get._id, DateTime.now, None, Seq())
              postService insertPost postToInsert
            }
          } yield {
            Logger info s"Create Thread with title ${newTopicDTO.title}"
            Logger info s"HTML is: ${newTopicDTO.htmlContent}"
            Ok(toJson(InsertTopicResultDTO(insertedThread._id))).as(JSON_TYPE)
          }
        })
  }

  def showTopic(id: Int) = OptionalSessionAction.async {
    request =>
      implicit val userOpt = request.maybeUser
      for {
        thread <- threadService.getThreadForApi(id)
        forum <- forumService.getForumForApi(thread.forum)
        posts <- postService.getPostsByThreadForApi
        userIndex <- userService.getUserIndexForApi
      } yield Ok(toJson(createShowThread(thread, forum, posts, userIndex))).as(JSON_TYPE)
  }

}