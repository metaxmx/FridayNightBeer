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

@Singleton
class ForumsController @Inject() (implicit usersService: UsersService, sessionsService: SessionsService, forumsService: ForumsService) extends Controller with MongoController with Secured {

  def getForums = Action.async {
    withSession[AnyContent] {
      authInfo =>
        request =>
          forumsService.getForumsDTO.map { result => Ok(toJson(result.categories)).as("application/json") }
    }
  }

}