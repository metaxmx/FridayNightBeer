package models

import org.joda.time.DateTime
import permissions.{Authorization, ThreadPermissions}
import util.Joda.dateTimeOrdering

case class ThreadPostData(user: String,
                          date: DateTime)


case class Thread(_id: String,
                  title: String,
                  forum: String,
                  threadStart: ThreadPostData,
                  lastPost: ThreadPostData,
                  posts: Int,
                  sticky: Boolean,
                  closed: Boolean,
                  threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Thread] {

  lazy val threadPermissionMap = threadPermissions.getOrElse(Map.empty)

  def withId(_id: String) = copy(_id = _id)

  def withLastPost(lastPost: ThreadPostData) = copy(lastPost = lastPost)

  def checkAccess(implicit authorization: Authorization, category: ForumCategory, forum: Forum): Boolean =
    authorization.checkThreadPermission(category, forum, this, ThreadPermissions.Access) &&
      forum.checkAccess

  def checkAccess(category: ForumCategory, forum: Forum)(implicit authorization: Authorization): Boolean =
    checkAccess(authorization, category, forum)
}

object Thread {

  private[this] val explicitTupleOrdering = Ordering.Tuple3(
    implicitly[Ordering[Boolean]].reverse, // First order sticky threads to the beginning
    implicitly[Ordering[DateTime]].reverse, // Then order by last post time (descending)
    implicitly[Ordering[String]]) // Fallback on id, if all else is equal

  implicit val threadOrdering: Ordering[Thread] = Ordering.by {
    thread: Thread =>
      (thread.sticky, thread.lastPost.date, thread._id)
  }(explicitTupleOrdering)

}
