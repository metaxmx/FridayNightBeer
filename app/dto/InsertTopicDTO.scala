package dto

import play.api.libs.json.Json

import models.Forum

case class InsertTopicRequestDTO(
  id: Int,
  title: String)

object InsertTopicRequestDTO {

  implicit val jsonFormat = Json.format[InsertTopicRequestDTO]

  def fromForum(forum: Forum) = InsertTopicRequestDTO(forum._id, forum.name)

}

case class InsertTopicDTO(
  title: String,
  htmlContent: String)

object InsertTopicDTO {

  implicit val jsonFormat = Json.format[InsertTopicDTO]

}

case class InsertTopicResultDTO(
  id: Int)

object InsertTopicResultDTO {

  implicit val jsonFormat = Json.format[InsertTopicResultDTO]

}