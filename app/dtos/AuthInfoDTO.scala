package dtos

import play.api.libs.json.Json
import models.User

case class AuthInfoDTO(
  authenticated: Boolean,
  userId: String,
  name: String,
  username: String) {

  def this() = this(false, null, null, null)

  def this(user: User) = this(true, user._id.toString(), user.displayName, user.username)

}

object AuthInfoDTO {

  implicit val jsonFormat = Json.format[AuthInfoDTO]

  def unauthenticated = new AuthInfoDTO

  def authenticated(user: User) = new AuthInfoDTO(user)

  def of(userOpt: Option[User]) = userOpt match {
    case None       => new AuthInfoDTO
    case Some(user) => new AuthInfoDTO(user)
  }

}