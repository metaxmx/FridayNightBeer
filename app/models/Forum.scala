package models

import play.api.libs.json.Json

case class Forum(
  _id: Int,
  name: String,
  description: String,
  category: Int,
  position: Int,
  readonly: Boolean)

object Forum {

  implicit val format = Json.format[Forum]

}