package models

import play.api.libs.json.Json

case class ForumCategory(
  _id: Int,
  name: String,
  position: Int)

object ForumCategory {

  implicit val format = Json.format[ForumCategory]

}