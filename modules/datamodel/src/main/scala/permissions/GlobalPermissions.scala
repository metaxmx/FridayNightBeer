package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object GlobalPermissions extends PermissionType {

  sealed abstract class GlobalPermission extends PermissionEnum

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
