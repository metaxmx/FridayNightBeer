package models

import org.joda.time.DateTime

import play.api.libs.json.Json

import reactivemongo.bson.Macros
import util.Joda.bsonHandler

case class UploadedImageData(
  format: String,
  width: Int,
  height: Int)

object UploadedImageData {

  implicit val bsonFormat = Macros.handler[UploadedImageData]

  implicit val jsonFormat = Json.format[UploadedImageData]

}

case class PostUpload(
  user: Int,
  date: DateTime,
  filename: String,
  source: String,
  size: Long,
  imageData: Option[UploadedImageData],
  hits: Int)

object PostUpload {

  implicit val bsonFormat = Macros.handler[PostUpload]

  implicit val jsonFormat = Json.format[PostUpload]

}