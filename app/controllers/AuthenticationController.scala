package controllers

import javax.inject.{Inject, Singleton}

import dto.{AuthInfoResultDTO, LoginRequestDTO}
import models.User
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json.toJson
import play.api.mvc.Controller
import services.{PermissionService, SessionService, UserService}
import util.PasswordEncoder

import scala.concurrent.Future

@deprecated("building of new API", "2016-05-11")
@Singleton
class AuthenticationController @Inject()(implicit val userService: UserService,
                                         val sessionService: SessionService,
                                         val permissionService: PermissionService) extends Controller with SecuredController {

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText)(LoginRequestDTO.apply)(LoginRequestDTO.unapply))

  def getAuthInfo = OptionalSessionApiAction.async {
    implicit request =>
      byAuthenticationStatus map {
        status =>  Ok(toJson(status)).as("application/json")
      }
  }

  def login = SessionApiAction.async(parse.json) {
    implicit request =>
      validateApiForm(loginForm) {
        loginRequest =>
          val passwordEncoded = PasswordEncoder encodePassword loginRequest.password
          userService.getUserByUsername(loginRequest.username.toLowerCase).filter {
            _.password == passwordEncoded
          }.fold(unauthenticated) {
           user =>
              // Store User in Session
              sessionService.updateSessionUser(request.userSession._id, Some(user)).toFuture.flatMap {
                _ => authenticated(user)
              }
          }.map {
            authInfoDto => Ok(toJson(authInfoDto))
          }
      }
  }

  def logout = SessionApiAction.async {
    request =>
      sessionService.updateSessionUser(request.userSession._id, None).toFuture flatMap {
        // TODO: Create new session ID
        _ => unauthenticated map {
          status => Ok(toJson(status))
        }
      }
  }

  private def unauthenticated: Future[AuthInfoResultDTO] = permissionService.createAuthorization()(None) map {
    auth => new AuthInfoResultDTO(auth.listGlobalPermissions)
  }

  private def authenticated(user: User): Future[AuthInfoResultDTO] = permissionService.createAuthorization()(Some(user)) map {
    auth => new AuthInfoResultDTO(user, auth.listGlobalPermissions)
  }

  private def byAuthenticationStatus(implicit userOpt: Option[User]) = userOpt match {
    case None => unauthenticated
    case Some(user) => authenticated(user)
  }

}