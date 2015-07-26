package models

import org.joda.time.DateTime

import play.api.libs.json.Json

import reactivemongo.bson.Macros
import util.Joda.bsonHandler

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
  edits: Option[Seq[PostEdit]],
  uploads: Seq[PostUpload]) {

  def withId(_id: Int) = Post(_id, thread, text, userCreated, dateCreated, edits, uploads)

}

object Post {

  implicit val bsonFormat = Macros.handler[Post]

  implicit val jsonFormat = Json.format[Post]

  implicit val baseModel = BaseModel[Post]("posts")

  implicit val postIdReader = new BaseModelIdReader[Post, Int] {
    def getId = _._id
  }

  implicit val postIdWriter = new BaseModelIdWriter[Post, Int] {
    def withId = _ withId _
  }

}

