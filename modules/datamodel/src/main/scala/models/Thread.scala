package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import reactivemongo.bson.Macros
import util.Joda.bsonHandler
import models.TopicPermissions.TopicPermission

case class ThreadPostData(
  user: Int,
  date: DateTime)

object ThreadPostData {

  implicit val bsonFormat = Macros.handler[ThreadPostData]

  implicit val jsonFormat = Json.format[ThreadPostData]

}

case class Thread(
    _id: Int,
    title: String,
    forum: Int,
    threadStart: ThreadPostData,
    lastPost: ThreadPostData,
    posts: Int,
    sticky: Boolean,
    restriction: Option[AccessRule]) {

  def accessGranted(implicit userOpt: Option[User]) = restriction map { _.allowed } getOrElse true

  def withId(_id: Int) = Thread(_id, title, forum, threadStart, lastPost, posts, sticky, restriction)

  def withLastPost(lastPost: ThreadPostData) = Thread(_id, title, forum, threadStart, lastPost, posts, sticky, restriction)

}

object Thread {

  implicit val bsonFormat = Macros.handler[Thread]

  implicit val jsonFormat = Json.format[Thread]

  implicit val baseModel = BaseModel[Thread]("threads")

  implicit val threadIdReader = new BaseModelIdReader[Thread, Int] {
    def getId = _._id
  }

  implicit val threadIdWriter = new BaseModelIdWriter[Thread, Int] {
    def withId = _ withId _
  }

  implicit val spec = new BaseModelImplicitSpec

}

