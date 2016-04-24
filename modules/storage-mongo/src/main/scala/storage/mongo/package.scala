package storage

import models._
import org.joda.time.{DateTime, DateTimeZone}
import reactivemongo.bson.{BSONDateTime, BSONHandler, Macros}

package object mongo {

  implicit def bsonJodaHandler = new BSONHandler[BSONDateTime, DateTime] {

    def read(time: BSONDateTime) = new DateTime(time.value, DateTimeZone.UTC)

    def write(jodaTime: DateTime) = BSONDateTime(jodaTime.getMillis)

  }

  implicit val bsonFormatAccessRule = Macros.handler[AccessRule]

  implicit val bsonFormatForumCategory = Macros.handler[ForumCategory]

  implicit val bsonFormatForum = Macros.handler[Forum]

  implicit val bsonFormatGroup = Macros.handler[Group]

  implicit val bsonFormatUser = Macros.handler[User]

  implicit val bsonFormatUploadedImageData = Macros.handler[UploadedImageData]

  implicit val bsonFormatPostUpload = Macros.handler[PostUpload]

  implicit val bsonFormatPostEdit = Macros.handler[PostEdit]

  implicit val bsonFormatPost = Macros.handler[Post]

  implicit val bsonFormatThreadPostData = Macros.handler[ThreadPostData]

  implicit val bsonFormatThread = Macros.handler[Thread]

  implicit val bsonFormatSession = Macros.handler[UserSession]

}
