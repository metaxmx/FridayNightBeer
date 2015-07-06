package dto

import play.api.libs.json.Json
import models.Forum
import models.ForumCategory
import models.User
import models.Thread
import util.Joda.dateTimeOrdering
import services.ForumsAndCategories
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

  def createListForums(forumsAndCats: ForumsAndCategories, threads: Map[Int, Seq[Thread]], users: Seq[User])(implicit userOpt: Option[User]): Seq[ListForumsCategory] = {
    val forums = forumsAndCats.forums filter { _.accessGranted }
    val categories = forumsAndCats.categories filter { _.accessGranted }
    val threadsByForum = threads mapValues { _.filter { _.accessGranted } }
    val forumsByCategory = forums groupBy { _.category } mapValues { _ sortBy { _.position } }
    val usersById = users.map { user => (user._id, user) }.toMap
    val listForumsByCategory = forumsByCategory mapValues {
      _ map {
        forum =>
          val threadsForForum = threadsByForum(forum._id)
          val lastPost = threadsForForum.sortBy { _.lastPost.date }.reverse.headOption
          val numThreads = threadsForForum.size
          val numPosts = threadsForForum.map { _.posts }.sum
          val listFrumLastPost = lastPost.filter {
            thread => usersById.contains(thread.lastPost.user)
          }.map { thread => ListForumsLastPost.fromThread(thread, usersById(thread.lastPost.user)) }
          ListForumsForum.fromForum(forum, numThreads, numPosts, listFrumLastPost)
      }
    }
    categories sortBy { _.position } map {
      c => ListForumsCategory(c.name, listForumsByCategory.getOrElse(c._id, Seq()))
    } filter { !_.forums.isEmpty }
  }

}

