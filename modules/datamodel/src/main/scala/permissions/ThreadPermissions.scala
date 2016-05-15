package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object ThreadPermissions extends PermissionType {

  sealed abstract class ThreadPermission extends PermissionEnum

  case object Access extends ThreadPermission

  case object Reply extends ThreadPermission

  val values = Seq(Access, Reply)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object ThreadPermission {

    def apply(name: String): ThreadPermission = valuesByName(name)

    def unapply(permission: ThreadPermission): Option[String] = Some(permission.name)

  }

}
