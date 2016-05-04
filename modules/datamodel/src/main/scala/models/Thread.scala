package models

import org.joda.time.DateTime

case class ThreadPostData(user: String,
                          date: DateTime)


case class Thread(_id: String,
                  title: String,
                  forum: String,
                  threadStart: ThreadPostData,
                  lastPost: ThreadPostData,
                  posts: Int,
                  sticky: Boolean,
                  threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Thread] {

  lazy val threadPermissionMap = threadPermissions.getOrElse(Map.empty)

  def withId(_id: String) = copy(_id = _id)

  def withLastPost(lastPost: ThreadPostData) = copy(lastPost = lastPost)

}
