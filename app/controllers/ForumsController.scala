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
import services.ForumsAndCategories
import dto.ListForumsAggregation.createListLorums

@Singleton
class ForumsController @Inject() (implicit usersService: UsersService, sessionsService: SessionsService, forumsService: ForumsService) extends Controller with MongoController with Secured {

  def getForums = Action.async {
    withSession[AnyContent] {
      authInfo =>
        request =>
          forumsService.getForumsAndCategories map {
            forumsAndCats => Ok(toJson(filterForumsByPermissions(forumsAndCats, authInfo._2))).as("application/json")
          }
    }
  }

  def filterForumsByPermissions(forumsAndCats: ForumsAndCategories, userOpt: Option[User]): Seq[ListForumsCategory] = {
    val forums = forumsAndCats.forums filter { _.accessGranted(userOpt) }
    val categories = forumsAndCats.categories filter { _.accessGranted(userOpt) }
    createListLorums(categories, forums)
  }

}