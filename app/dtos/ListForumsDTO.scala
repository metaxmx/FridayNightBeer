package dtos

import play.api.libs.json.Json
import models.ForumCategory
import models.Forum

case class ListForumsForum(
  name: String,
  description: String)

object ListForumsForum {

  implicit val jsonFormat = Json.format[ListForumsForum]

  def fromForum(forum: Forum) = ListForumsForum(forum.name, forum.description)

}

case class ListForumsCategory(
  name: String,
  forums: Seq[ListForumsForum])

object ListForumsCategory {

  implicit val jsonFormat = Json.format[ListForumsCategory]

}

case class ListForumsDTO(categories: Seq[ListForumsCategory])

object ListForumsDTO {

  implicit val jsonFormat = Json.format[ListForumsDTO]

  def createFromModels(categories: Seq[ForumCategory], forums: Seq[Forum]): ListForumsDTO = {
    val forumsByCategory = forums groupBy { _.category } mapValues { _ sortBy { _.position } }
    ListForumsDTO(categories map { c => ListForumsCategory(c.name, forumsByCategory.getOrElse(c._id, Seq()) map { ListForumsForum.fromForum(_) }) })
  }

}



