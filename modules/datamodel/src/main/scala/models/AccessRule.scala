package models

import permissions.AuthorizationPrincipal

/**
  * Permissions:
  * ------------
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
  */

/**
  * General trait for classes to allow or deny access to a permission.
  */
trait PermissionRule {

  /**
    * Check if the permission rule is allowed for the given user (or None for a guest).
    *
    * @param userOpt some user or None for guest
    * @return true if permission is granted
    */
  def allowed(implicit userOpt: Option[User]): Boolean

  /**
    * Check if the permission rule is allowed for the given authorization.
    *
    * @param principal authorization principal
    * @return true if permission is granted
    */
  def allowed(implicit principal: AuthorizationPrincipal): Boolean = allowed(principal.userOpt)

}

/**
  * Chain of access rules.
  *
  * @param accessRules sequence of access rules in the chain. The first element in the sequence is
  *                    the most general one (e.g. default permission), while the last one is the most
  *                    specific permission (e.g. for the exact object).
  */
case class AccessRuleChain(accessRules: Seq[AccessRule]) extends PermissionRule {

  override def allowed(implicit userOpt: Option[User]): Boolean = AccessRule.accessRuleChainAllowed(this)

}

/**
  * Access Rule to define permissions.
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
                      allowAll: Option[Boolean]) extends PermissionRule {

  override def allowed(implicit userOpt: Option[User]): Boolean = AccessRule.accessRuleAllowed(this)

}

/**
  * Companion object to [[AccessRule]].
  */
object AccessRule {

  def accessRuleAllowed(ar: AccessRule)(implicit userOpt: Option[User]): Boolean =
    ar.allowAll.contains(true) || userOpt.forall {
      implicit user => !userExcluded(ar) && (ar.allowAllUsers.contains(true) || userIncluded(ar))
    }

  def accessRuleChainAllowed(ac: AccessRuleChain)(implicit userOpt: Option[User]): Boolean = {
    val state = (PermissionCheckState() /: ac.accessRules) {
      (state, nextRule) =>
        val nextState = PermissionCheckState(
          allowAll = nextRule.allowAll getOrElse state.allowAll,
          allowUsers = nextRule.allowAllUsers getOrElse state.allowUsers,
          forbiddenUsers = nextRule.forbiddenUsers getOrElse state.forbiddenUsers,
          forbiddenGroups = nextRule.forbiddenGroups getOrElse state.forbiddenGroups,
          allowedUsers = nextRule.allowedUsers getOrElse state.allowedUsers,
          allowedGroups = nextRule.allowedGroups getOrElse state.allowedGroups
        )
        nextState.copy(permitted = stateAllowed(nextState))
    }
    state.permitted
  }

  private[this] def stateAllowed(st: PermissionCheckState)(implicit userOpt: Option[User]): Boolean = {
    st.allowAll || userOpt.forall {
      implicit user => !userExcluded(st) && (st.allowUsers || userIncluded(st))
    }
  }

  private[this] case class PermissionCheckState(allowAll: Boolean = false,
                                                allowUsers: Boolean = false,
                                                forbiddenUsers: Seq[String] = Seq.empty,
                                                forbiddenGroups: Seq[String] = Seq.empty,
                                                allowedUsers: Seq[String] = Seq.empty,
                                                allowedGroups: Seq[String] = Seq.empty,
                                                permitted: Boolean = false)

  private[this] def userIncluded(ar: AccessRule)(implicit user: User): Boolean =
    (ar.allowedUsers exists (_ contains user._id)) || containsCommonElement(ar.allowedGroups, user.groups)

  private[this] def userIncluded(st: PermissionCheckState)(implicit user: User): Boolean =
    (st.allowedUsers contains user._id) || containsCommonElement(st.allowedGroups, user.groups)

  private[this] def userExcluded(ar: AccessRule)(implicit user: User): Boolean =
    (ar.forbiddenUsers exists (_ contains user._id)) || containsCommonElement(ar.forbiddenGroups, user.groups)

  private[this] def userExcluded(st: PermissionCheckState)(implicit user: User): Boolean =
    (st.forbiddenUsers contains user._id) || containsCommonElement(st.forbiddenGroups, user.groups)

  private[this] def containsCommonElement(seq1: Option[Seq[String]], seq2: Option[Seq[String]]): Boolean =
    seq1 exists (s1 => containsCommonElement(s1, seq2))

  private[this] def containsCommonElement(s1: Seq[String], seq2: Option[Seq[String]]): Boolean =
    seq2 exists (s2 => (s1 intersect s2).nonEmpty)


}
