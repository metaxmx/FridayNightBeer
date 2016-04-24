package models

import org.joda.time.DateTime

case class UploadedImageData(format: String,
                             width: Int,
                             height: Int)

case class PostUpload(user: Int,
                      date: DateTime,
                      filename: String,
                      source: String,
                      size: Long,
                      imageData: Option[UploadedImageData],
                      hits: Int)

