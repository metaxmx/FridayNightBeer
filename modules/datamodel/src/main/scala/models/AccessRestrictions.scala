package models

import play.api.libs.json.Json

import reactivemongo.bson.Macros

case class AccessRestriction(
  forbiddenUsers: Option[Seq[Int]],
  forbiddenGroups: Option[Seq[Int]],
  allowedUsers: Option[Seq[Int]],
  allowedGroups: Option[Seq[Int]],
  allowAnonymous: Option[Boolean]) {

  def allowed(implicit userOpt: Option[User]): Boolean = userOpt.fold {
    // If no user logged in, check anonymous access
    allowAnonymous getOrElse true
  } {
    // If user is logged in:
    // 1) Deny access if user matched by forbiddenUsers or in forbiddenGroups
    // 2) if allowedUsers and/or allowedGroups is defined: Allow only if user matches the users or groups 
    implicit user => !userExcluded && userIncluded
  }

  private def userExcluded(implicit user: User) =
    (forbiddenUsers exists { _ contains user._id }) ||
      (containsCommonElement(forbiddenGroups, user.groups))

  private def userIncluded(implicit user: User) =
    (!allowedUsers.isDefined && !allowedGroups.isDefined) ||
      (allowedUsers exists { _ contains user._id }) ||
      (containsCommonElement(allowedGroups, user.groups))

  private def containsCommonElement(seq1: Option[Seq[Int]], seq2: Option[Seq[Int]]): Boolean =
    seq1.isDefined && seq2.isDefined && !(seq1.get.intersect(seq2.get).isEmpty)

}

object AccessRestriction {

  implicit val bsonFormat= Macros.handler[AccessRestriction]
  
  implicit val jsonFormat = Json.format[AccessRestriction]
  
}