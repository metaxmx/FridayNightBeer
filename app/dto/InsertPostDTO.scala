package dto

import play.api.libs.json.Json

case class InsertPostDTO(
  content: String)

object InsertPostDTO {

  implicit val jsonFormat = Json.format[InsertPostDTO]

}

case class InsertPostErrorDTO(
    errors: Seq[String])
    
object InsertPostErrorDTO {

  implicit val jsonFormat = Json.format[InsertPostErrorDTO]

}