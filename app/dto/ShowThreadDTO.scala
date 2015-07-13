package dto

import org.joda.time.DateTime

import play.api.libs.json.Json

import models.{ Forum, Post, Thread, User }
import util.Joda.dateTimeOrdering

case class ShowThreadPostDTO(id: Int, date: DateTime)

object ShowThreadPostDTO {

  implicit val jsonFormat = Json.format[ShowThreadPostDTO]

  def fromPost(post: Post, user: User) =
    ShowThreadPostDTO(post._id, post.dateCreated)

}

case class ShowThreadDTO(
  id: Int,
  title: String,
  forum: Int,
  forumTitle: String,
  posts: Seq[ShowThreadPostDTO])

object ShowThreadDTO {

  implicit val jsonFormat = Json.format[ShowThreadDTO]

  def fromThread(thread: Thread, forum: Forum, posts: Seq[ShowThreadPostDTO]) =
    ShowThreadDTO(thread._id, thread.title, forum._id, forum.name, posts)

}

object ShowThreadAggregation {

  def createShowThread(thread: Thread, forum: Forum, posts: Map[Int, Seq[Post]], userIndex: Map[Int, User])(implicit userOpt: Option[User]): ShowThreadDTO = {
    val postsForThread = posts.get(thread._id).getOrElse(Seq())
    val postDTOs = postsForThread.map {
      post =>
        // TODO: Check if user exists
        val user = userIndex(post.userCreated)
        ShowThreadPostDTO.fromPost(post, user)
    }
    val postDTOsSorted = postDTOs.sortBy { _.date }
    ShowThreadDTO.fromThread(thread, forum, postDTOsSorted)
  }

}