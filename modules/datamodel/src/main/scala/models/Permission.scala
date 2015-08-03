package models

import scala.annotation.implicitNotFound

import play.api.libs.json.{ Format, JsPath, JsResult, JsString, JsValue }

import reactivemongo.bson.{ BSON, BSONHandler, BSONString }

trait SealedPermissionEnum {
  val name = toString
}

object GlobalPermissions {

  sealed abstract class GlobalPermission extends SealedPermissionEnum

  case object Forums extends GlobalPermission
  case object Media extends GlobalPermission
  case object Events extends GlobalPermission
  case object Members extends GlobalPermission
  case object Admin extends GlobalPermission

  val values = Seq(Forums, Media, Events, Members, Admin)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object GlobalPermission {

    def apply(name: String): GlobalPermission = valuesByName(name)

    def unapply(permission: GlobalPermission): Option[String] = Some(permission.name)

    implicit val jsonFormat = new Format[GlobalPermission] {
      def reads(json: JsValue): JsResult[GlobalPermission] = JsPath.read[String].map(GlobalPermission apply _).reads(json)
      def writes(o: GlobalPermission): JsValue = JsString(o.name)
    }

    implicit val bsonHandler = new BSONHandler[BSONString, GlobalPermission] {
      def read(doc: BSONString) = GlobalPermission(doc.value)
      def write(o: GlobalPermission) = BSON.write(o.name)
    }

  }

}

object ForumPermissions {

  sealed abstract class ForumPermission extends SealedPermissionEnum

  case object Access extends ForumPermission
  case object NewTopic extends ForumPermission
  case object Reply extends ForumPermission
  case object Sticky extends ForumPermission
  case object Close extends ForumPermission

  val values = Seq(Access, NewTopic, Reply, Sticky, Close)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object ForumPermission {

    def apply(name: String): ForumPermission = valuesByName(name)

    def unapply(permission: ForumPermission): Option[String] = Some(permission.name)

    implicit val jsonFormat = new Format[ForumPermission] {
      def reads(json: JsValue): JsResult[ForumPermission] = JsPath.read[String].map(ForumPermission apply _).reads(json)
      def writes(o: ForumPermission): JsValue = JsString(o.name)
    }

    implicit val bsonHandler = new BSONHandler[BSONString, ForumPermission] {
      def read(doc: BSONString) = ForumPermission(doc.value)
      def write(o: ForumPermission) = BSON.write(o.name)
    }

  }

}

object TopicPermissions {

  sealed abstract class TopicPermission extends SealedPermissionEnum

  case object Access extends TopicPermission

  val values = Seq(Access)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object TopicPermission {

    def apply(name: String): TopicPermission = valuesByName(name)

    def unapply(permission: TopicPermission): Option[String] = Some(permission.name)

    implicit val jsonFormat = new Format[TopicPermission] {
      def reads(json: JsValue): JsResult[TopicPermission] = JsPath.read[String].map(TopicPermission apply _).reads(json)
      def writes(o: TopicPermission): JsValue = JsString(o.name)
    }

    implicit val bsonHandler = new BSONHandler[BSONString, TopicPermission] {
      def read(doc: BSONString) = TopicPermission(doc.value)
      def write(o: TopicPermission) = BSON.write(o.name)
    }

  }

}

