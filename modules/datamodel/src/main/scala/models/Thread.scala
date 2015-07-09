package models

import play.api.libs.json.Json
import util.Joda.bsonHandler
import org.joda.time.DateTime
import reactivemongo.bson.Macros

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
  restriction: Option[AccessRestriction]) {

  def accessGranted(implicit userOpt: Option[User]) = restriction map { _.allowed } getOrElse true
  
  def withId(_id: Int) = Thread(_id, title, forum, threadStart, lastPost, posts, sticky, restriction)

}

object Thread extends BaseModel {

  implicit val bsonFormat = Macros.handler[Thread]

  implicit val jsonFormat = Json.format[Thread]

  def collectionName = "threads"
  
  implicit val threadIdReader = new BaseModelIdReader[Thread] {
    def getId = _._id
  }
  
  implicit val threadIdWriter = new BaseModelIdWriter[Thread] {
    def withId = _ withId _
  }

}

