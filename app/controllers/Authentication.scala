package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.{ Action, AnyContent, Controller }

import dto.{ AuthInfoDTO, LoginParams }
import services.{ SessionService, UserService }
import util.PasswordEncoder

@Singleton
class Authentication @Inject() (implicit userService: UserService,
                                sessionsService: SessionService) extends Controller with Secured {

  def getAuthInfo = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          Future.successful(Ok(toJson(AuthInfoDTO.of(sessionInfo.userOpt))).as("application/json"))
    }
  }

  def login = Action.async(parse.json) {
    withSession {
      sessionInfo =>
        request =>
          request.body.validate[LoginParams].map {
            loginParams =>
              val passwordEncoded = PasswordEncoder.encodePassword(loginParams.password)
              userService.getUserByUsername(loginParams.username.toLowerCase) map {
                userOpt =>
                  {
                    Logger.info(s"Found user ${userOpt}")
                    userOpt filter (_.password == passwordEncoded)
                  }
              } flatMap {
                case None => Future.successful(AuthInfoDTO.unauthenticated)
                case Some(user) => {
                  // Store User in Session
                  sessionsService.updateSessionUser(sessionInfo.session._id, Some(user)) map {
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
      sessionInfo =>
        request =>
          sessionsService.updateSessionUser(sessionInfo.session._id, None) map {
            // TODO: Create new session ID
            _ => Ok(toJson(AuthInfoDTO.unauthenticated))
          }
    }
  }

}