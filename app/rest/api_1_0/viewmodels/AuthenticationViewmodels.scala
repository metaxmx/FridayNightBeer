package rest.api_1_0.viewmodels

/**
  * View Models for Authentication.
  * Created by Christian on 04.05.2016.
  */
object AuthenticationViewModels {

  case class LoginRequest(username: String,
                          password: String) extends ViewModel

  case class LoginResult(success: Boolean,
                         sessionId: String,
                         userId: Option[String] = None) extends ViewModel

  case class LogoutResult(success: Boolean) extends ViewModel

}
