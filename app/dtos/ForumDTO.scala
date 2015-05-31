package dtos

import play.api.libs.json.Json

case class ForumDTO(
  pk: Long,
  title: String,
  description: String)

object ForumDTO {

  implicit val jsonFormat = Json.format[ForumDTO]

}