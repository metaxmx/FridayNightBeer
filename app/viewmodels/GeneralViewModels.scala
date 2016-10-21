package viewmodels

import models.AccessRule

/**
  * Created by Christian on 21.10.2016.
  */
object GeneralViewModels {

  /*
   * Permissions / Access Rules
   */

  type PermissionMap = Map[String, AccessRuleViewModel]
  type OptPermissionMap = Option[PermissionMap]

  case class AccessRuleViewModel(forbiddenUsers: Option[Seq[String]],
                                 forbiddenGroups: Option[Seq[String]],
                                 allowedUsers: Option[Seq[String]],
                                 allowedGroups: Option[Seq[String]],
                                 allowAllUsers: Option[Boolean],
                                 allowAll: Option[Boolean]) extends ViewModel

  object AccessRuleViewModel {

    def fromViewModel(viewModel: AccessRuleViewModel): AccessRule =
      AccessRule(viewModel.forbiddenUsers, viewModel.forbiddenGroups, viewModel.allowedUsers, viewModel.allowedGroups,
        viewModel.allowAllUsers, viewModel.allowAll)

    def fromViewModel(permissionMap: PermissionMap): Map[String, AccessRule] = permissionMap mapValues fromViewModel

    def fromViewModel(optPermissionMap: OptPermissionMap): Option[Map[String, AccessRule]] = optPermissionMap map fromViewModel

    def toViewModel(rule: AccessRule): AccessRuleViewModel =
      AccessRuleViewModel(rule.forbiddenUsers, rule.forbiddenGroups, rule.allowedUsers, rule.allowedGroups,
        rule.allowAllUsers, rule.allowAll)

    def toViewModel(permissionMap: Map[String, AccessRule]): PermissionMap = permissionMap mapValues toViewModel

    def toViewModel(optPermissionMap: Option[Map[String, AccessRule]]): OptPermissionMap = optPermissionMap map toViewModel

  }

}
