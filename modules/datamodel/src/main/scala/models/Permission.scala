package models

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

  }

}

