package dto

import models.Forum
import play.api.libs.json.Json

case class ConfigureForumsForum(
  id: Int,
  name: String,
  description: Option[String],
  position: Int,
  empty: Boolean)

object ConfigureForumsForum {

  implicit val jsonFormat = Json.format[ConfigureForumsForum]

  def fromForum(forum: Forum, empty: Boolean) =
    ConfigureForumsForum(forum._id, forum.name, forum.description, forum.position, empty)

}

case class ConfigureForumsCategory(
  id: Int,
  name: String,
  position: Int,
  forums: Seq[ConfigureForumsForum])

object ConfigureForumsCategory {

  implicit val jsonFormat = Json.format[ConfigureForumsCategory]

}

