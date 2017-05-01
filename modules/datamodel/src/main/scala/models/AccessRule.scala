package models

import scala.annotation.tailrec


/**
  * Access Rule to define permissions.
  *
  * Permissions are defined by four categories:
  * - (1) All (including Guests) allowed
  * - (2) All Users allowed
  * - (3) Some users and/or groups allowed
  * - (4) Some users and/or groups forbidden
  *
  * The general rule of thumb is described by the following ranks of the permission rules (more important rules on the left):
  *
  * all (1) > some forbidden (4) > some allowed (3) > all users (2)
  *
  * If there is more than one permission rule (from general rules to specialized rules), the general settings
  * for the settings (1)-(4) can be overridden on each level to the more specific rules.
  *
  * @param forbiddenUsers  set of [[User]] ids which are never permitted
  * @param forbiddenGroups set of [[Group]] ids from which users are never permitted
  * @param allowedUsers    set of [[User]] ids which are permitted, unless forbidden explicitly
  * @param allowedGroups   set of [[Group]] ids from which users are permitted, unless forbidden explicitly
  * @param allowAllUsers   optional flag if all logged-in users are permitted, unless forbidden explicitly.
  * @param allowAll        optional flag if everyone including guests (not logged in users) are permitted.
  *                        If this flag is true, no-one can be forbidden in this stage of the chain.
  *                        If this flag is false, any previous allowAll flags in the chain are invalidated
  */
case class AccessRule(forbiddenUsers: Option[Seq[String]],
                      forbiddenGroups: Option[Seq[String]],
                      allowedUsers: Option[Seq[String]],
                      allowedGroups: Option[Seq[String]],
                      allowAllUsers: Option[Boolean],
                      allowAll: Option[Boolean]) {

  def withAllowAllUsers: AccessRule = copy(allowAllUsers = Some(true))

  def withDenyAllUsers: AccessRule = copy(allowAllUsers = Some(false))

  def withAllowAll: AccessRule = copy(allowAll = Some(true))

  def withDenyAll: AccessRule = copy(allowAll = Some(false))

  def withAllowUsers(userIds: String*): AccessRule = copy(allowedUsers = Some(userIds))

  def withAllowGroups(groupIds: String*): AccessRule = copy(allowedGroups = Some(groupIds))

  def withForbiddenUsers(userIds: String*): AccessRule = copy(forbiddenUsers = Some(userIds))

  def withForbiddenGroups(groupIds: String*): AccessRule = copy(forbiddenGroups = Some(groupIds))

  val isAllowAll: Boolean = allowAll.getOrElse(false)
  val isAllowAllUsers: Boolean = allowAllUsers.getOrElse(false)
  val allowedUserSet: Set[String] = allowedUsers.fold(Set.empty[String])(_.toSet)
  val allowedGroupSet: Set[String] = allowedGroups.fold(Set.empty[String])(_.toSet)
  val forbiddenUserSet: Set[String] = forbiddenUsers.fold(Set.empty[String])(_.toSet)
  val forbiddenGroupSet: Set[String] = forbiddenGroups.fold(Set.empty[String])(_.toSet)

  /**
    * Return new access rule, containing each parameter from this rule, with a fallback to the
    * corresponding parameter from the other access rule.
    * @param fallback other access rule to combine with
    * @return new access rule containing the combination
    */
  def withFallback(fallback: AccessRule): AccessRule = {
    AccessRule(
      forbiddenUsers = forbiddenUsers orElse fallback.forbiddenUsers,
      forbiddenGroups = forbiddenGroups orElse fallback.forbiddenGroups,
      allowedUsers = allowedUsers orElse fallback.allowedUsers,
      allowedGroups = allowedGroups orElse fallback.allowedGroups,
      allowAllUsers = allowAllUsers orElse fallback.allowAllUsers,
      allowAll = allowAll orElse fallback.allowAll
    )
  }

  /**
    * Return new access rule, containing each parameter from the other rule if defined, with a fallback to the
    * corresponding parameter from this access rule.
    * @param other other access rule to combine with
    * @return new access rule containing the combination
    */
  def overrideWith(other: AccessRule): AccessRule = other.withFallback(this)

}

/**
  * Companion object to [[AccessRule]].
  */
object AccessRule {

  def empty: AccessRule = AccessRule(None, None, None, None, None, None)

}

/**
  * Chain of access rules.
  *
  * @param accessRules sequence of access rules in the chain. The first element in the sequence is
  *                    the most general one (e.g. default permission), while the last one is the most
  *                    specific permission (e.g. for the exact object).
  */
case class AccessRuleChain(accessRules: Seq[AccessRule]) {

  /**
    * Combine the chain of access rules to a single access rule containing the evaluated logic of the access rules in the chain.
    * @return single access rule
    */
  def reduceToSingleRule: AccessRule = {
    @tailrec
    def combineChain(rules: List[AccessRule]): AccessRule = rules match {
      case Nil => AccessRule.empty
      case head :: Nil => head
      case first :: second :: tail => combineChain(first.overrideWith(second) :: tail)
    }
    combineChain(accessRules.toList)
  }

}

object AccessRuleChain {

  def empty = AccessRuleChain(Seq.empty)

  def apply(accessRule: Option[AccessRule], accessRules: Option[AccessRule]*): AccessRuleChain = {
    AccessRuleChain((accessRule +: accessRules).flatten)
  }

}
