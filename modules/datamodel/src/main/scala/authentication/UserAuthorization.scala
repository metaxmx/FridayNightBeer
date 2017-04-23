package authentication
import models.AccessRule

/**
  * Authorization object implementation for a user profile.
  * @param userProfile user profile to authorize
  */
class UserAuthorization(userProfile: UserProfile) extends AccessRuleAuthorization {

  override def authorize(ar: AccessRule): Boolean = userProfile match {
    case auth: AuthenticatedProfile =>
      authorizeAuthenticated(ar, auth.userId, auth.groups)
    case UnauthenticatedProfile =>
      authorizeUnauthenticated(ar)
  }

  /**
    * Authorize unauthenticated user.
    * @param ar access rule
    * @return true if authorization was successful
    */
  private def authorizeUnauthenticated(ar: AccessRule): Boolean = {
    ar.isAllowAll
  }

  /**
    * Authorize authenticated profile
    * @param ar access rule
    * @param userId user id of profile
    * @param groups set of group ids of profile
    * @return true if authorization was successful
    */
  private def authorizeAuthenticated(ar: AccessRule, userId: String, groups: Set[String]) = {
    def userIncluded: Boolean = ar.allowedUserSet.contains(userId) || (ar.allowedGroupSet intersect groups).nonEmpty
    def userExcluded: Boolean = ar.forbiddenUserSet.contains(userId) || (ar.forbiddenGroupSet intersect groups).nonEmpty

    ar.isAllowAll || (!userExcluded && (ar.isAllowAllUsers || userIncluded))
  }

}
