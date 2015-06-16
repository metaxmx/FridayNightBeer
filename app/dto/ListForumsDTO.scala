package dto

import play.api.libs.json.Json
import models.Forum
import models.ForumCategory

case class ListForumsLastPost(
  id: Int,
  name: String)

case class ListForumsForum(
  id: Int,
  name: String,
  description: String)

object ListForumsForum {

  implicit val jsonFormat = Json.format[ListForumsForum]

  def fromForum(forum: Forum) = ListForumsForum(forum._id, forum.name, forum.description)

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



