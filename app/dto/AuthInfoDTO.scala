package dto

import play.api.libs.json.Json

import models.User

case class AuthInfoDTO(
    authenticated: Boolean,
    userId: String,
    name: String,
    username: String,
    globalPermissions: Seq[String]) {

  def this(globalPermissions: Seq[String]) = this(false, null, null, null, globalPermissions)

  def this(user: User, globalPermissions: Seq[String]) = this(true, user._id.toString(), user.displayName, user.username, globalPermissions)

}

object AuthInfoDTO {

  implicit val jsonFormat = Json.format[AuthInfoDTO]

}