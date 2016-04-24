package dto

import play.api.libs.json.Json

import models.User

case class AuthInfoResultDTO(
    authenticated: Boolean,
    userId: String,
    name: String,
    username: String,
    globalPermissions: Seq[String]) {

  def this(globalPermissions: Seq[String]) = this(false, null, null, null, globalPermissions)

  def this(user: User, globalPermissions: Seq[String]) = this(true, user._id, user.displayName, user.username, globalPermissions)

}

object AuthInfoResultDTO {

  implicit val jsonFormat = Json.format[AuthInfoResultDTO]

}