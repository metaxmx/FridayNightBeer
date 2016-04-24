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
                  restriction: Option[AccessRule]) extends BaseModel[Thread] {

  def accessGranted(implicit userOpt: Option[User]) = restriction forall (_.allowed)

  def withId(_id: String) = copy(_id = _id)

  def withLastPost(lastPost: ThreadPostData) = copy(lastPost = lastPost)

}
