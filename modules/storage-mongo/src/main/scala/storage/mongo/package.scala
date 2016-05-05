package storage

import models._
import org.joda.time.{DateTime, DateTimeZone}
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONHandler, Macros}

package object mongo {

  implicit val bsonJodaHandler = new BSONHandler[BSONDateTime, DateTime] {

    def read(time: BSONDateTime) = new DateTime(time.value, DateTimeZone.UTC)

    def write(jodaTime: DateTime) = BSONDateTime(jodaTime.getMillis)

  }

  implicit val bsonFormatAccessRule = Macros.handler[AccessRule]

  implicit val bsonAccessRuleMapHandler = new BSONHandler[BSONDocument, Map[String, AccessRule]] {

    override def read(bson: BSONDocument): Map[String, AccessRule] = {
      bson.elements.toMap.mapValues {
        _.seeAsOpt[BSONDocument] map { bsonFormatAccessRule.read }
      } filter {
        case (key, value) => value.isDefined
      } mapValues(_.get)
    }

    override def write(map: Map[String, AccessRule]): BSONDocument = {
      BSONDocument(map.mapValues(bsonFormatAccessRule.write))
    }
  }

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

  implicit val bsonFormatPermission = Macros.handler[Permission]

}
