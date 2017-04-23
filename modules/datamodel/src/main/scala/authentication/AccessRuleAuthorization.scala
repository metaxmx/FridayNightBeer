package authentication

import models.{AccessRule, AccessRuleChain}

import scala.language.implicitConversions

/**
  * Authorization object to authorize access rules and access rule chains.
  */
trait AccessRuleAuthorization {

  /**
    * Authorize single access rule.
    * @param rule rule to authorize
    * @return true if authorization succeeded
    */
  def authorize(rule: AccessRule): Boolean

  /**
    * Authorize access rule chain.
    * @param chain chain to authorize
    * @return true if authorization succeeded
    */
  def authorize(chain: AccessRuleChain): Boolean = authorize(chain.reduceToSingleRule)

}

object AccessRuleAuthorization {

  /**
    * Create authorization from user profile.
    * @param userProfile user profile to authorize
    * @return
    */
  implicit def deriveUserAuthorization(userProfile: UserProfile): AccessRuleAuthorization = new UserAuthorization(userProfile)

}