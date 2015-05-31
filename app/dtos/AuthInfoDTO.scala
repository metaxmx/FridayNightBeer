package dtos

import play.api.libs.json.Json

case class AuthInfoDTO(
  authenticated: Boolean,
  userId: String,
  name: String,
  username: String)

object AuthInfoDTO {

  implicit val jsonFormat = Json.format[AuthInfoDTO]

}