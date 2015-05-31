package dtos

import play.api.libs.json.Json

case class AuthInfoDTO(
  authenticated: Boolean,
  userPk: Long,
  name: String,
  username: String)

object AuthInfoDTO {

  implicit val jsonFormat = Json.format[AuthInfoDTO]

}