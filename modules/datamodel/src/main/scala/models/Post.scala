package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class PostEdit(
  user: Int,
  date: DateTime,
  reason: Option[String],
  ip: String)
  
object PostEdit {
  implicit val format = Json.format[PostEdit]
}

case class Post(
  _id: Int,
  thread: Int,
  text: String,
  userCreated: Int,
  dateCreated: DateTime,
  edits: Option[Seq[PostEdit]])

object Post extends BaseModel {

  implicit val format = Json.format[Post]

  def collectionName = "posts"

}

