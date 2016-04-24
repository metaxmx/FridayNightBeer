package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import org.joda.time.DateTime

import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{ mapping, nonEmptyText }
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import dto.{ InsertPostDTO, InsertPostErrorDTO, InsertTopicDTO, InsertTopicRequestDTO, InsertTopicResultDTO }
import dto.ShowThreadAggregation.createShowThread
import models.{ Post, Thread, ThreadPostData, User }
import services.{ ForumService, PostService, SessionService, ThreadService, UserService }

@Singleton
class TopicController @Inject() (implicit val userService: UserService,
                                 val sessionService: SessionService,
                                 forumService: ForumService,
                                 threadService: ThreadService,
                                 postService: PostService) extends Controller with SecuredController {

  def showNewTopic(id: String) = UserApiAction.async {
    implicit request =>
      for {
        forum <- forumService.getForumForApi(id)
      } yield Ok(toJson(InsertTopicRequestDTO.fromForum(forum))).as(JSON)

  }

  def newTopic(id: String) = UserApiAction.async(parse.json) {
    implicit request =>
      request.body.validate[InsertTopicDTO].fold(
        error => Future.successful(BadRequest("Bad JSON format")),
        newTopicDTO => {
          for {
            forum <- forumService.getForumForApi(id)
            insertedThread <- {
              val threadToInsert = Thread("", newTopicDTO.title, forum._id,
                ThreadPostData(request.user._id, DateTime.now),
                ThreadPostData(request.user._id, DateTime.now), 1, false, None)
              threadService insertThread threadToInsert
            }
            insertedPost <- {
              val postToInsert = Post("", insertedThread._id, newTopicDTO.htmlContent,
                request.user._id, DateTime.now, None, Seq())
              postService insertPost postToInsert
            }
          } yield {
            Logger info s"Create Thread with title ${newTopicDTO.title}"
            Logger info s"HTML is: ${newTopicDTO.htmlContent}"
            Ok(toJson(InsertTopicResultDTO(insertedThread._id))).as(JSON)
          }
        })
  }

  def showTopic(id: String) = OptionalSessionApiAction.async {
    implicit request =>
      showTopicData(id)
  }

  private def showTopicData(id: String)(implicit maybeUser: Option[User]) = for {
    thread <- threadService.getThreadForApi(id)
    forum <- forumService.getForumForApi(thread.forum)
    posts <- postService.getPostsByThreadForApi
    userIndex <- userService.getUserIndexForApi
  } yield Ok(toJson(createShowThread(thread, forum, posts, userIndex))).as(JSON)

  val insertPostForm = Form(
    mapping(
      "content" -> nonEmptyText)(InsertPostDTO.apply)(InsertPostDTO.unapply))

  def insertPost(id: String) = UserApiAction.async(parse.json) {
    implicit request =>
      insertPostForm.bindFromRequest.fold(
        formWithErrors => Future.successful(BadRequest(toJson(InsertPostErrorDTO(formWithErrors.errors.map(_.message))))),
        insertPostDTO =>
          for {
            thread <- threadService.getThreadForApi(id)
            forum <- forumService.getForumForApi(thread.forum)
            posts <- postService.getPostsByThreadForApi
            userIndex <- userService.getUserIndexForApi
            insertedPost <- {
              val postToInsert = Post("", thread._id, insertPostDTO.content,
                request.user._id, DateTime.now, None, Seq())
              postService insertPost postToInsert
            }
            updatedThread <- threadService.updateLastPostForApi(id, insertedPost.userCreated, insertedPost.dateCreated)
            result <- showTopicData(id)
          } yield result)
  }

}