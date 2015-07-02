package models

import org.joda.time.DateTime
import util.Joda.bsonHandler
import play.api.libs.json.Json
import reactivemongo.bson.Macros

case class PostEdit(
  user: Int,
  date: DateTime,
  reason: Option[String],
  ip: String)

object PostEdit {

  implicit val bsonFormat = Macros.handler[PostEdit]

  implicit val jsonFormat = Json.format[PostEdit]

}

case class Post(
  _id: Int,
  thread: Int,
  text: String,
  userCreated: Int,
  dateCreated: DateTime,
  edits: Option[Seq[PostEdit]])

object Post extends BaseModel {

  implicit val bsonFormat = Macros.handler[Post]

  implicit val jsonFormat = Json.format[Post]

  def collectionName = "posts"

}

