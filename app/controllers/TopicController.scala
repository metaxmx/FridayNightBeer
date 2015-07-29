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
class TopicController @Inject() (implicit userService: UserService,
                                 sessionsService: SessionService,
                                 forumService: ForumService,
                                 threadService: ThreadService,
                                 postService: PostService) extends Controller with Secured {

  def showNewTopic(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          forumService.getForumForApi(id) map {
            forum => Ok(toJson(InsertTopicRequestDTO.fromForum(forum))).as(JSON_TYPE)
          } recover {
            case e: ApiException => e.result
          }
    }
  }

  def newTopic(id: Int) = Action.async(parse.json) {
    withSession[JsValue] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          request.body.validate[InsertTopicDTO].fold(
            error => Future.successful(BadRequest("Bad JSON format")),
            newTopicDTO => {
              val dataFuture = for {
                forum <- forumService.getForumForApi(id)
                insertedThread <- {
                  val threadToInsert = Thread(0, newTopicDTO.title, forum._id,
                    ThreadPostData(sessionInfo.userOpt.get._id, DateTime.now),
                    ThreadPostData(sessionInfo.userOpt.get._id, DateTime.now), 1, false, None)
                  threadService insertThread threadToInsert
                }
                insertedPost <- {
                  val postToInsert = Post(0, insertedThread._id, newTopicDTO.htmlContent,
                    sessionInfo.userOpt.get._id, DateTime.now, None, Seq())
                  postService insertPost postToInsert
                }
              } yield (forum, insertedThread, insertedPost)
              dataFuture map {
                case (forum, insertedThread, insertedPost) =>
                  Logger info s"Create Thread with title ${newTopicDTO.title}"
                  Logger info s"HTML is: ${newTopicDTO.htmlContent}"
                  Ok(toJson(InsertTopicResultDTO(insertedThread._id))).as(JSON_TYPE)
              } recover {
                case e: ApiException => e.result
              }
            })
    }
  }

  def showTopic(id: Int) = Action.async {
    withSessionOption[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          val dataFuture = for {
            thread <- threadService.getThreadForApi(id)
            forum <- forumService.getForumForApi(thread.forum)
            posts <- postService.getPostsByThreadForApi
            userIndex <- userService.getUserIndexForApi
          } yield (thread, forum, posts, userIndex)
          dataFuture map {
            case (thread, forum, posts, userIndex) =>
              Ok(toJson(createShowThread(thread, forum, posts, userIndex))).as(JSON_TYPE)
          } recover {
            case e: ApiException => e.result
          }
    }
  }

}