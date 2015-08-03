package dto

import org.joda.time.DateTime

import play.api.libs.json.Json

import models.{ Forum, Thread, User }

case class ListForumsLastPost(
  id: Int,
  title: String,
  user: Int,
  userName: String,
  date: DateTime)

object ListForumsLastPost {

  implicit val jsonFormat = Json.format[ListForumsLastPost]

  def fromThread(thread: Thread, user: User) =
    ListForumsLastPost(thread._id, thread.title, user._id, user.displayName, thread.lastPost.date)
}

case class ListForumsForum(
  id: Int,
  name: String,
  description: Option[String],
  numThreads: Int,
  numPosts: Int,
  lastPost: Option[ListForumsLastPost])

object ListForumsForum {

  implicit val jsonFormat = Json.format[ListForumsForum]

  def fromForum(forum: Forum, numThreads: Int, numPosts: Int, lastPost: Option[ListForumsLastPost]) =
    ListForumsForum(forum._id, forum.name, forum.description, numThreads, numPosts, lastPost)

}

case class ListForumsCategory(
  name: String,
  forums: Seq[ListForumsForum])

object ListForumsCategory {

  implicit val jsonFormat = Json.format[ListForumsCategory]

}

