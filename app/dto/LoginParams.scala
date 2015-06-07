package dto

import play.api.libs.json.Json

case class LoginParams(
  username: String,
  password: String)

object LoginParams {

  implicit def format = Json.format[LoginParams]

}