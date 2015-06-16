package models

import play.api.libs.json.Json

case class Forum(
  _id: Int,
  name: String,
  description: String,
  category: Int,
  position: Int,
  readonly: Boolean,
  restriction: Option[AccessRestriction]) {

  def accessGranted(userOpt: Option[User]) = restriction.isEmpty || restriction.get.allowed(userOpt)

}

object Forum {
  implicit val format = Json.format[Forum]
}

case class ForumCategory(
  _id: Int,
  name: String,
  position: Int,
  restriction: Option[AccessRestriction]) {

  def accessGranted(userOpt: Option[User]) = restriction.isEmpty || restriction.get.allowed(userOpt)

}

object ForumCategory {
  implicit val format = Json.format[ForumCategory]
}