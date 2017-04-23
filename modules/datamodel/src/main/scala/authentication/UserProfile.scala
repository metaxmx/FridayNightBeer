package authentication

import models.User

/**
  * User authentication profile.
  */
sealed trait UserProfile {

  /** true if profile is for an authenticated user */
  def authenticated: Boolean = userOpt.isDefined

  /** Optional user id of profile */
  def userIdOpt: Option[String] = userOpt.map(_._id)
  /** Optional user of profile */
  def userOpt: Option[User]

  /** IDs of Groups assigned to the profile */
  def groups: Set[String]

}

object UserProfile {

  /**
    * Factory for unauthenticated user profile.
    */
  def apply(): UserProfile = UnauthenticatedProfile

  /**
    * Factory for authenticated user profile.
    */
  def apply(user: User): UserProfile = AuthenticatedProfile(user)

  /**
    * Factory for user profile, depending on argument value.
    */
  def apply(userOpt: Option[User]): UserProfile = userOpt match {
    case None => apply()
    case Some(user) => apply(user)
  }

}

/**
  * User Profile for unauthenticated user.
  */
case object UnauthenticatedProfile extends UserProfile {

  def userOpt = None

  def groups = Set.empty

}

/**
  * User Profile for authenticated user
  * @param user user model
  */
case class AuthenticatedProfile(user: User) extends UserProfile {

  def userId = user._id

  def userOpt = Some(user)

  val groups = user.groups.map(_.toSet).getOrElse(Set.empty)

}