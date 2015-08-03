package dto

import org.joda.time.DateTime

import play.api.libs.json.Json

import models.{ Forum, Thread, User }
import util.Joda.dateTimeOrdering

case class ShowForumPost(
  user: Int,
  userName: String,
  date: DateTime)

object ShowForumPost {

  implicit val jsonFormat = Json.format[ShowForumPost]

}

case class ShowForumThread(
  id: Int,
  title: String,
  posts: Int,
  sticky: Boolean,
  firstPost: ShowForumPost,
  latestPost: ShowForumPost)

object ShowForumThread {

  implicit val jsonFormat = Json.format[ShowForumThread]

  def fromThread(thread: Thread, firstPost: ShowForumPost, latestPost: ShowForumPost) =
    ShowForumThread(thread._id, thread.title, thread.posts, thread.sticky, firstPost, latestPost)
}

case class ShowForumDTO(
  id: Int,
  title: String,
  threads: Seq[ShowForumThread],
  permissions: Seq[String])

object ShowForumDTO {

  implicit val jsonFormat = Json.format[ShowForumDTO]

  def fromForum(forum: Forum, threads: Seq[ShowForumThread], permissions: Seq[String]) =
    ShowForumDTO(forum._id, forum.name, threads, permissions)

}
