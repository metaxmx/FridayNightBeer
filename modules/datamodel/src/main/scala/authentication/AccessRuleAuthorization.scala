package authentication

import models.{AccessRule, AccessRuleChain}

import scala.language.implicitConversions

/**
  * Authorization object to authorize access rules and access rule chains.
  * @param userProfile user profile to authorize
  */
class AccessRuleAuthorization(userProfile: UserProfile) {

  /**
    * Authorize access rule chain.
    * @param chain chain to authorize
    * @return true if authorization succeeded
    */
  def authorize(chain: AccessRuleChain): Boolean = authorize(chain.reduceToSingleRule)

  /**
    * Authorize single access rule.
    * @param rule rule to authorize
    * @return true if authorization succeeded
    */
  def authorize(rule: AccessRule): Boolean = userProfile match {
    case auth: AuthenticatedProfile =>
      authorizeAuthenticated(rule, auth.userId, auth.groups)
    case UnauthenticatedProfile =>
      authorizeUnauthenticated(rule)
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

object AccessRuleAuthorization {

  /**
    * Create authorization from user profile.
    * @param userProfile user profile to authorize
    * @return authorization
    */
  implicit def buildAuthorization(userProfile: UserProfile): AccessRuleAuthorization = new AccessRuleAuthorization(userProfile)

}