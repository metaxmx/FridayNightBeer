package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, AnyContent, Controller }
import play.modules.reactivemongo.MongoController

import dto.ShowThreadAggregation.createShowThread
import services.{ ForumService, PostService, SessionService, ThreadService, UserService }

@Singleton
class TopicController @Inject() (implicit userService: UserService,
                                 sessionsService: SessionService,
                                 forumService: ForumService,
                                 threadService: ThreadService,
                                 postService: PostService) extends Controller with MongoController with Secured {

  def showTopic(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          implicit val userOpt = sessionInfo.userOpt
          threadService.getThread(id) flatMap {
            case None => Future.successful(NotFound("Thread not Found"))
            case Some(thread) =>
              if (!thread.accessGranted)
                Future.successful(Forbidden("Access to thread denied"))
              else {
                forumService.getForum(thread.forum) flatMap {
                  case None => Future.successful(NotFound("Forum not Found"))
                  case Some(forum) =>
                    if (!forum.accessGranted)
                      Future.successful(Forbidden("Access to forum denied"))
                    else {
                      val dataFuture = for {
                        posts <- postService.getPostsByThread
                        userIndex <- userService.getUserIndex
                      } yield (posts, userIndex)
                      dataFuture map {
                        case (posts, userIndex) => Ok(toJson(createShowThread(thread, forum, posts, userIndex))).as("application/json")
                      }
                    }
                }
              }
          }
    }
  }

}