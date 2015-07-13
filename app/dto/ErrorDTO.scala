package dto

import play.api.libs.json.Json

case class ErrorDTO(error: String)

object ErrorDTO {

  implicit val jsonFormat = Json.format[ErrorDTO]

}