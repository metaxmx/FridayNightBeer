package dto

import play.api.libs.json.Json
import models.Forum
import models.ForumCategory
import models.User
import models.Thread
import util.Joda.dateTimeOrdering
import org.joda.time.DateTime

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

object ListForumsAggregation {

  def createListForums(allCategories: Seq[ForumCategory], allForumsByCategory: Map[Int, Seq[Forum]],
                       threads: Map[Int, Seq[Thread]], userIndex: Map[Int, User])(implicit userOpt: Option[User]): Seq[ListForumsCategory] = {
    val categories = allCategories filter { _.accessGranted } sortBy { _.position }
    val forumsByCategory = allForumsByCategory mapValues { _ filter { _.accessGranted } sortBy { _.position } }
    val threadsByForum = threads mapValues { _ filter { _.accessGranted } }
    val listForumsByCategory = forumsByCategory mapValues {
      _ map {
        forum =>
          val threadsForForum = threadsByForum.getOrElse(forum._id, Seq())
          val lastPost = threadsForForum.sortBy { _.lastPost.date }.reverse.headOption
          val numThreads = threadsForForum.size
          val numPosts = threadsForForum.map { _.posts }.sum
          val listForumLastPost = lastPost.filter { userIndex contains _.lastPost.user } map {
            thread => ListForumsLastPost.fromThread(thread, userIndex(thread.lastPost.user))
          }
          ListForumsForum.fromForum(forum, numThreads, numPosts, listForumLastPost)
      }
    }
    categories map {
      c => ListForumsCategory(c.name, listForumsByCategory.getOrElse(c._id, Seq()))
    } filter { !_.forums.isEmpty }
  }

}

