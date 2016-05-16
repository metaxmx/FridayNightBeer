package rest.api_1_0.viewmodels

/**
  * View Models for Authentication.
  * Created by Christian on 04.05.2016.
  */
object AuthenticationViewModels {

  case class LoginRequest(username: String,
                          password: String) extends ViewModel

  case class LoginResult(success: Boolean,
                         authenticationStatus: AuthenticationStatus) extends ViewModel

  case class LogoutResult(success: Boolean,
                          authenticationStatus: AuthenticationStatus) extends ViewModel

  case class AuthenticationStatusUser(id: String,
                                      username: String,
                                      displayName: String,
                                      fullName: Option[String],
                                      avatar: Option[String],
                                      groups: Option[Seq[String]]) extends ViewModel

  case class AuthenticationStatus(authenticated: Boolean,
                                  sessionId: Option[String],
                                  user: Option[AuthenticationStatusUser],
                                  globalPermissions: Seq[String]) extends ViewModel

  case class GetAuthenticationStatusResult(success: Boolean,
                                           authenticationStatus: AuthenticationStatus) extends ViewModel

}
