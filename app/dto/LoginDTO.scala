package dto

import play.api.libs.json.Json

case class LoginRequestDTO(
  username: String,
  password: String)
