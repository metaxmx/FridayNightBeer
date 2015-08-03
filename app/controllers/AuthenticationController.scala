package controllers

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller

import dto.{ AuthInfoDTO, LoginParams }
import exceptions.ApiExceptions.badRequestException
import models.User
import services.{ PermissionService, SessionService, UserService }
import util.PasswordEncoder

@Singleton
class AuthenticationController @Inject() (implicit val userService: UserService,
                                val sessionService: SessionService,
                                permissionService: PermissionService) extends Controller with SecuredController {

  def getAuthInfo = OptionalSessionApiAction {
    implicit request =>
      Logger.info(request.maybeUser.toString())
      Ok(toJson(byAuthenticationStatus)).as("application/json")
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
            case None => Future.successful(unauthenticated)
            case Some(user) => {
              // Store User in Session
              sessionService.updateSessionUser(request.userSession._id, Some(user)) map {
                _ => authenticated(user)
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
        _ => Ok(toJson(unauthenticated))
      }
  }

  private def unauthenticated = new AuthInfoDTO(permissionService.getAllowedGlobalPermissions(None).map(_.name))

  private def authenticated(user: User) = new AuthInfoDTO(user, permissionService.getAllowedGlobalPermissions(Some(user)).map(_.name))

  private def byAuthenticationStatus(implicit userOpt: Option[User]) = userOpt match {
    case None       => unauthenticated
    case Some(user) => authenticated(user)
  }

}