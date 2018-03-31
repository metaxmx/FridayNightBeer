package permissions

/**
  * Permissions for Thread actions.
  */
sealed abstract class ThreadPermission extends PermissionEnum

/**
  * Permissions for Thread actions.
  */
object ThreadPermission extends PermissionType[ThreadPermission] {

  case object Access extends ThreadPermission

  case object Reply extends ThreadPermission

  case object EditPost extends ThreadPermission

  case object DeletePost extends ThreadPermission

  case object Attachment extends ThreadPermission

  val values = Seq(Access, Reply, EditPost, DeletePost, Attachment)

}
