package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{ LoggerFactory, Logger }
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import dtos.AuthInfoDTO
import javax.inject.Inject
import services.UsersService
import dto.LoginParams
import services.SessionsService
import services.SessionsService
import services.PasswordEncoder

@Singleton
class Authentication @Inject() (implicit usersService: UsersService, sessionsService: SessionsService) extends Controller with MongoController with Secured {

  def getAuthInfo = Action.async {
    withSession[AnyContent] {
      authInfo =>
        request =>
          val result = authInfo match {
            case Tuple2(session, userOpt) => AuthInfoDTO.of(userOpt)
          }
          Future.successful(Ok(toJson(result)).as("application/json"))
    }
  }

    def login = Action.async(parse.json) {
      request =>
        request.body.validate[LoginParams].map {
          loginParams =>
            val passwordEncoded = PasswordEncoder.encodePassword(loginParams.password)
            usersService.findUserByUsername(loginParams.username) map {
              userOpt => userOpt filter(_.password == passwordEncoded)
            } map {
              case None => AuthInfoDTO(false, null, null, null)
              case Some(user) => {
                // Store User in Session
                
                
                AuthInfoDTO(true, user._id.toString(), user.displayName, user.username)
              }
            } map {
              authInfoDto => Ok(toJson(authInfoDto))
            }
        }.getOrElse(Future.successful(BadRequest("invalid json")))
    }

}