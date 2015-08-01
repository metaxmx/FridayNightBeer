package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import dto.{ AuthInfoDTO, LoginParams }
import exceptions.ApiExceptions.badRequestException
import services.{ SessionService, UserService }
import util.PasswordEncoder

@Singleton
class Authentication @Inject() (implicit val userService: UserService,
                                val sessionService: SessionService) extends Controller with SecuredController {

  def getAuthInfo = OptionalSessionApiAction {
    request =>
      Ok(toJson(AuthInfoDTO.of(request.maybeUser))).as("application/json")
  }

  def login = SessionApiAction.async(parse.json) {
    request =>
      request.body.validate[LoginParams].map {
        loginParams =>
          val passwordEncoded = PasswordEncoder encodePassword loginParams.password
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
              sessionService.updateSessionUser(request.userSession._id, Some(user)) map {
                _ => AuthInfoDTO.authenticated(user)
              }
            }
          } map {
            authInfoDto => Ok(toJson(authInfoDto))
          }
      } getOrElse badRequestException
  }

  def logout = SessionApiAction.async {
    request =>
      sessionService.updateSessionUser(request.userSession._id, None) map {
        // TODO: Create new session ID
        _ => Ok(toJson(AuthInfoDTO.unauthenticated))
      }
  }

}