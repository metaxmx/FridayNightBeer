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
  threads: Seq[ShowForumThread])

object ShowForumDTO {

  implicit val jsonFormat = Json.format[ShowForumDTO]

  def fromForum(forum: Forum, threads: Seq[ShowForumThread]) =
    ShowForumDTO(forum._id, forum.name, threads)

}

object ShowForumAggregation {

  def createShowForum(forum: Forum, threads: Map[Int, Seq[Thread]], userIndex: Map[Int, User])(implicit userOpt: Option[User]): ShowForumDTO = {
    val visibleThreads = threads.get(forum._id).getOrElse(Seq()).filter { _.accessGranted }
    val threadDTOs = visibleThreads.map {
      thread =>
        // TODO: Check if user exists
        val firstPostUser = userIndex(thread.threadStart.user)
        val firstPost = ShowForumPost(firstPostUser._id, firstPostUser.displayName, thread.threadStart.date)
        val latestPortUser = userIndex(thread.lastPost.user)
        val latestPost = ShowForumPost(latestPortUser._id, latestPortUser.displayName, thread.lastPost.date)
        ShowForumThread.fromThread(thread, firstPost, latestPost)
    }
    val threadDTOsSorted = threadDTOs.sortBy { _.latestPost.date }.reverse.sortWith((a, b) => a.sticky && !b.sticky)
    ShowForumDTO.fromForum(forum, threadDTOsSorted)
  }

}