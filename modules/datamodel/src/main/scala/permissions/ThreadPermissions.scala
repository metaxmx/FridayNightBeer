package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object ThreadPermissions extends PermissionType {

  sealed abstract class ThreadPermission extends PermissionEnum

  override def permissionType = classOf[ThreadPermission]

  case object Access extends ThreadPermission

  case object Reply extends ThreadPermission

  case object EditPost extends ThreadPermission

  case object DeletePost extends ThreadPermission

  case object Attachment extends ThreadPermission

  val values = Seq(Access, Reply, EditPost, DeletePost, Attachment)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object ThreadPermission {

    def apply(name: String): ThreadPermission = valuesByName(name)

    def unapply(permission: ThreadPermission): Option[String] = Some(permission.name)

  }

}
