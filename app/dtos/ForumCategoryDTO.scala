package dtos

import play.api.libs.json.Json

case class ForumCategoryDTO(
  title: String,
  forums: Seq[ForumDTO])

object ForumCategoryDTO {

  implicit val jsonFormat = Json.format[ForumCategoryDTO]

}