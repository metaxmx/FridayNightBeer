package dto

import play.api.libs.json.Json

case class InsertCategoryDTO(
  title: String)

object InsertCategoryDTO {

  implicit val jsonFormat = Json.format[InsertCategoryDTO]

}

