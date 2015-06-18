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

object ListForumsAggregation {

  def createListLorums(categories: Seq[ForumCategory], forums: Seq[Forum]): Seq[ListForumsCategory] = {
    val forumsByCategory = forums groupBy { _.category } mapValues { _ sortBy { _.position } }
    categories sortBy { _.position } map { c => ListForumsCategory(c.name, forumsByCategory.getOrElse(c._id, Seq()) map { ListForumsForum.fromForum(_) }) }
  }

}



