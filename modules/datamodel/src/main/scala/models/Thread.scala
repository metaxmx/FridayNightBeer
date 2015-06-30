package models

import play.api.libs.json.Json
import org.joda.time.DateTime

case class ThreadPostData(
  user: Int,
  date: DateTime)

object ThreadPostData {
  implicit val format = Json.format[ThreadPostData]
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

  def accessGranted(userOpt: Option[User]) = restriction.isEmpty || restriction.get.allowed(userOpt)

}

object Thread extends BaseModel {

  implicit val format = Json.format[Thread]

  def collectionName = "threads"

}

