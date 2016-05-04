package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object ForumPermissions extends PermissionType {

  sealed abstract class ForumPermission extends PermissionEnum

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
