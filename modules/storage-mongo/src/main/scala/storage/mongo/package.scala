package storage

import models.{AccessRule, ForumCategory}
import reactivemongo.bson.Macros

package object mongo {

  implicit val bsonFormatAccessRule = Macros.handler[AccessRule]

  implicit val bsonFormatForumCategory = Macros.handler[ForumCategory]

}
