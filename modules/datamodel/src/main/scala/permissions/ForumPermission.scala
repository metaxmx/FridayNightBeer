package permissions

/**
  * Permissions for Forum actions.
  */
sealed abstract class ForumPermission extends PermissionEnum

/**
  * Permissions for Forum actions.
  */
object ForumPermission extends PermissionType[ForumPermission] {

  case object Access extends ForumPermission

  case object CreateThread extends ForumPermission

  case object Sticky extends ForumPermission

  case object Close extends ForumPermission

  case object DeleteThread extends ForumPermission

  val values = Seq(Access, CreateThread, Sticky, Close, DeleteThread)

}
