package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
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
import play.api.Logger

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
    withSession {
      case (session, _) =>
        request =>
          request.body.validate[LoginParams].map {
            loginParams =>
              val passwordEncoded = PasswordEncoder.encodePassword(loginParams.password)
              Logger.info(s"Trying login with user ${loginParams.username} and password hash ${passwordEncoded}")
              usersService.findUserByUsername(loginParams.username) map {
                userOpt =>
                  {
                    Logger.info(s"Found user ${userOpt}")
                    userOpt filter (_.password == passwordEncoded)
                  }
              } flatMap {
                case None => Future.successful(AuthInfoDTO.unauthenticated)
                case Some(user) => {
                  // Store User in Session
                  sessionsService.updateSessionUser(session._id, Some(user)) map {
                    _ => AuthInfoDTO.authenticated(user)
                  }
                }
              } map {
                authInfoDto => Ok(toJson(authInfoDto))
              }
          }.getOrElse(Future.successful(BadRequest("invalid json")))
    }
  }

  def logout = Action.async {
    withSession[AnyContent] {
      case (session, _) =>
        request =>
          sessionsService.updateSessionUser(session._id, None) map {
            // TODO: Create new session ID
            _ => Ok(toJson(AuthInfoDTO.unauthenticated))
          }
    }
  }

}