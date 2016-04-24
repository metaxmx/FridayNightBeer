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

@Singleton
class AuthenticationController @Inject()(implicit val userService: UserService,
                                         val sessionService: SessionService,
                                         permissionService: PermissionService) extends Controller with SecuredController {

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText)(LoginRequestDTO.apply)(LoginRequestDTO.unapply))

  def getAuthInfo = OptionalSessionApiAction {
    implicit request =>
      Ok(toJson(byAuthenticationStatus)).as("application/json")
  }

  def login = SessionApiAction.async(parse.json) {
    implicit request =>
      validateApiForm(loginForm) {
        loginRequest =>
          val passwordEncoded = PasswordEncoder encodePassword loginRequest.password
          userService.getUserByUsername(loginRequest.username.toLowerCase) map {
            _ filter (_.password == passwordEncoded)
          } flatMap {
            case None => Future.successful(unauthenticated)
            case Some(user) =>
              // Store User in Session
              sessionService.updateSessionUser(request.userSession._id, Some(user)) map {
                _ => authenticated(user)
              }

          } map {
            authInfoDto => Ok(toJson(authInfoDto))
          }
      }
  }

  def logout = SessionApiAction.async {
    request =>
      sessionService.updateSessionUser(request.userSession._id, None) map {
        // TODO: Create new session ID
        _ => Ok(toJson(unauthenticated))
      }
  }

  private def unauthenticated = new AuthInfoResultDTO(permissionService.getAllowedGlobalPermissions(None).map(_.name))

  private def authenticated(user: User) = new AuthInfoResultDTO(user, permissionService.getAllowedGlobalPermissions(Some(user)).map(_.name))

  private def byAuthenticationStatus(implicit userOpt: Option[User]) = userOpt match {
    case None => unauthenticated
    case Some(user) => authenticated(user)
  }

}