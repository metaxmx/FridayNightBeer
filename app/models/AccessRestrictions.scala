package models

import play.api.libs.json.Json

case class AccessRestriction(
  users: Option[Seq[Int]],
  groups: Option[Seq[Int]],
  anonymous: Option[Boolean],
  allow: Boolean) {

  def allowed(userOpt: Option[User]): Boolean = if (allow) {
    // Only allow access if in explicit "allowed" list:
    // Anonymous access or
    // User contained in user list or
    // User has a group which is in group list
    anonymous.exists { _ == true } || userOpt.exists {
      user =>
        users.exists { _ contains user._id } ||
          (user.groups.isDefined && groups.exists { !_.intersect(user.groups.get).isEmpty })
    }
  } else {
    // Allow access if not in explicit "denied" list
    // Anonymous access (denied if no user logged in) and
    // User not contained in user list and
    // User has no group which is in group list
    userOpt.fold {
      !anonymous.exists { _ == true }
    } {
      user =>
        (!users.exists { _ contains user._id }) &&
          (!(user.groups.isDefined && groups.exists { !_.intersect(user.groups.get).isEmpty }))
    }
  }

}

object AccessRestriction {
  implicit def format = Json.format[AccessRestriction]
}