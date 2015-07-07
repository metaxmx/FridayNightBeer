package controllers

import javax.inject.Singleton
import javax.inject.Inject
import services.ForumsService
import play.modules.reactivemongo.MongoController
import play.api.mvc._
import play.api.libs.json.Json.toJson
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import services.UsersService
import services.SessionsService
import dto.ListForumsCategory
import models.User
import models.Thread
import services.ForumsAndCategories
import dto.ListForumsAggregation.createListForums
import services.ThreadsService
import dto.ShowForumDTO
import dto.ShowForumAggregation.createShowForum
import dto.NewTopicDTO
import dto.ShowNewTopicDTO
import play.api.libs.json.JsValue
import play.Logger

@Singleton
class ForumsController @Inject() (implicit usersService: UsersService,
                                  sessionsService: SessionsService,
                                  forumsService: ForumsService,
                                  threadsService: ThreadsService) extends Controller with MongoController with Secured {

  def getForums = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          forumsService.getForumsAndCategories flatMap {
            forumsAndCats => threadsService.getThreadsByForum map { (forumsAndCats, _) }
          } flatMap {
            case (forumsAndCats, threads) => usersService.getUsers map { (forumsAndCats, threads, _) }
          } map {
            case (forumsAndCats, threads, users) => Ok(toJson(createListForums(forumsAndCats, threads, users)(sessionInfo.userOpt))).as("application/json")
          }
    }
  }

  def showForum(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          forumsService.findForum(id) flatMap {
            case None => Future.successful(NotFound("Forum not Found"))
            case Some(forum) =>
              if (!forum.accessGranted(sessionInfo.userOpt))
                Future.successful(Forbidden("Access to forum denied"))
              else
                threadsService.getThreadsByForum map { (forum, _) } flatMap {
                  case (forum, threads) => usersService.getUsers map { (forum, threads, _) }
                } map {
                  case (forum, threads, users) => Ok(toJson(createShowForum(forum, threads, users)(sessionInfo.userOpt))).as("application/json")
                }
          }
    }
  }

  def showNewTopic(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          forumsService.findForum(id) map {
            case None => NotFound("Forum not Found")
            case Some(forum) =>
              if (!forum.accessGranted(sessionInfo.userOpt))
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
          request.body.validate[NewTopicDTO].fold(
            error => Future.successful(BadRequest("Bad JSON format")),
            newTopicDTO => {
              Logger info s"Create Thread with title ${newTopicDTO.title}"
              Logger info s"HTML is: ${newTopicDTO.htmlContent}"
              Future.successful(Ok("TODO"))
            })
    }
  }

}