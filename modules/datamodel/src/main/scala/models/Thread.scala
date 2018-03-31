package models

import authentication.PermissionAuthorization
import org.joda.time.DateTime
import permissions.ThreadPermission
import util.JodaOrdering.dateTimeOrdering

case class ThreadPostData(user: String,
                          date: DateTime)


case class Thread(_id: String,
                  title: String,
                  url: Option[String],
                  forum: String,
                  threadStart: ThreadPostData,
                  lastPost: ThreadPostData,
                  posts: Int,
                  sticky: Boolean,
                  closed: Boolean,
                  threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Thread] {

  lazy val threadPermissionMap: Map[String, AccessRule] = threadPermissions.getOrElse(Map.empty)

  def withId(_id: String): Thread = copy(_id = _id)

  def withLastPost(lastPost: ThreadPostData): Thread = copy(lastPost = lastPost)

  def checkAccess(implicit authorization: PermissionAuthorization, category: ForumCategory, forum: Forum): Boolean =
    authorization.checkThreadPermissions(category, forum, this, ThreadPermission.Access) &&
      forum.checkAccess

  def checkAccess(category: ForumCategory, forum: Forum)(implicit authorization: PermissionAuthorization): Boolean =
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
