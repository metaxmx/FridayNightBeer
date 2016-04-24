package dto

import org.joda.time.DateTime
import play.api.libs.json.Json
import models.{ Forum, Post, Thread, User, PostUpload }
import util.Joda.dateTimeOrdering

case class ShowThreadPostUploadDTO(
  filename: String,
  size: Long,
  hits: Int)

object ShowThreadPostUploadDTO {

  implicit val jsonFormat = Json.format[ShowThreadPostUploadDTO]

  def fromUpload(upload: PostUpload) =
    ShowThreadPostUploadDTO(upload.filename, upload.size, upload.hits)

}

case class ShowThreadPostDTO(
  id: String,
  date: DateTime,
  user: String,
  userName: String,
  userFullname: Option[String],
  userAvatar: Boolean,
  content: String,
  uploads: Option[Seq[ShowThreadPostUploadDTO]])

object ShowThreadPostDTO {

  implicit val jsonFormat = Json.format[ShowThreadPostDTO]

  def fromPost(post: Post, user: User) =
    ShowThreadPostDTO(post._id, post.dateCreated, user._id, user.displayName, user.fullName, user.avatar.isDefined, post.text,
      if (post.uploads.isEmpty) None else Some(post.uploads.map(ShowThreadPostUploadDTO fromUpload _)))

}

case class ShowThreadDTO(
  id: String,
  title: String,
  forum: String,
  forumTitle: String,
  posts: Seq[ShowThreadPostDTO])

object ShowThreadDTO {

  implicit val jsonFormat = Json.format[ShowThreadDTO]

  def fromThread(thread: Thread, forum: Forum, posts: Seq[ShowThreadPostDTO]) =
    ShowThreadDTO(thread._id, thread.title, forum._id, forum.name, posts)

}

object ShowThreadAggregation {

  def createShowThread(thread: Thread, forum: Forum, posts: Map[String, Seq[Post]], userIndex: Map[String, User])(implicit userOpt: Option[User]): ShowThreadDTO = {
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