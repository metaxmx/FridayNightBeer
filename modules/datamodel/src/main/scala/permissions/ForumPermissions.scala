package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object ForumPermissions extends PermissionType {

  sealed abstract class ForumPermission extends PermissionEnum

  override def permissionType = classOf[ForumPermission]

  case object Access extends ForumPermission

  case object CreateThread extends ForumPermission

  case object Sticky extends ForumPermission

  case object Close extends ForumPermission

  case object DeleteThread extends ForumPermission

  val values = Seq(Access, CreateThread, Sticky, Close, DeleteThread)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object ForumPermission {

    def apply(name: String): ForumPermission = valuesByName(name)

    def unapply(permission: ForumPermission): Option[String] = Some(permission.name)

  }

}
