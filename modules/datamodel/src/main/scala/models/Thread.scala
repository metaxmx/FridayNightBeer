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

}

object Thread extends BaseModel {

  implicit val bsonFormat = Macros.handler[Thread]

  implicit val jsonFormat = Json.format[Thread]

  def collectionName = "threads"

}

