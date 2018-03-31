package permissions

/**
  * Global permissions.
  */
sealed abstract class GlobalPermission extends PermissionEnum

/**
  * Global permissions.
  */
object GlobalPermission extends PermissionType[GlobalPermission] {

  case object Forums extends GlobalPermission

  case object Media extends GlobalPermission

  case object Events extends GlobalPermission

  case object Members extends GlobalPermission

  case object Admin extends GlobalPermission

  val values = Seq(Forums, Media, Events, Members, Admin)

}
