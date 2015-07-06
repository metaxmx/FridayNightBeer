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
            case Some(forum) => threadsService.getThreadsByForum map { (forum, _) } flatMap {
              case (forum, threads) => usersService.getUsers map { (forum, threads, _) }
            } map {
              case (forum, threads, users) => Ok(toJson(createShowForum(forum, threads, users)(sessionInfo.userOpt))).as("application/json")
            }
          }
    }
  }

}