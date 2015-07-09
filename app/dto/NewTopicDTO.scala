package dto

import play.api.libs.json.Json
import org.joda.time.DateTime
import util.Joda.dateTimeOrdering
import models.Forum
import models.Thread
import models.User

case class ShowNewTopicDTO(
  id: Int,
  title: String)

object ShowNewTopicDTO {

  implicit val jsonFormat = Json.format[ShowNewTopicDTO]

  def fromForum(forum: Forum) = ShowNewTopicDTO(forum._id, forum.name)

}

case class NewTopicDTO(
  title: String,
  htmlContent: String)

object NewTopicDTO {

  implicit val jsonFormat = Json.format[NewTopicDTO]

}

case class InsertedTopicDTO(
  id: Int)

object InsertedTopicDTO {

  implicit val jsonFormat = Json.format[InsertedTopicDTO]

}