package models

import play.api.libs.json.Json

case class Forum(
  _id: Int,
  name: String,
  description: Option[String],
  category: Int,
  position: Int,
  readonly: Boolean,
  restriction: Option[AccessRestriction]) {

  def accessGranted(userOpt: Option[User]) = restriction.isEmpty || restriction.get.allowed(userOpt)

}

object Forum extends BaseModel {

  implicit val format = Json.format[Forum]

  def collectionName = "forums"

}