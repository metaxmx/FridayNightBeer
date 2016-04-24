package services

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import javax.inject.Singleton

import org.apache.commons.io.FileUtils.{readFileToString, writeStringToFile}
import play.api.Logger
import exceptions.ApiExceptions.accessDeniedException
import models.{AccessRule, Forum, ForumCategory, ForumPermissions}
import models.{PermissionConfiguration, User}
import models.ForumPermissions.{Access, Close, ForumPermission, Sticky}
import models.GlobalPermissions
import models.GlobalPermissions.{Admin, Forums, GlobalPermission}
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.native._

@Singleton
class PermissionService {

  implicit val formats = DefaultFormats

  // TODO: Move to DB
  val permissionsFile = new File("appdata/permissions.json")

  val permissions = validateAndAddDefaultPermissions(readPermissions())

  private def readPermissions(): PermissionConfiguration =
    if (permissionsFile.exists)
      JsonParser.parse(readFileToString(permissionsFile, UTF_8)).extract[PermissionConfiguration]
    else
      PermissionConfiguration(Seq(), Seq())

  def savePermissions() = {
    Logger.info("Saving permissions to file")
    writeStringToFile(permissionsFile, Serialization.writePretty(Extraction.decompose(permissions)))
  }

  // Save permissions of service initializcation
  savePermissions()

  private def validateAndAddDefaultPermissions(permissions: PermissionConfiguration): PermissionConfiguration = {
    val definedGlobalPermissions = permissions.globalPermissions.filter {
      accessRule =>
        if (GlobalPermissions.valuesByName.contains(accessRule.permission))
          true
        else {
          Logger.warn(s"Unknown global permission '${accessRule.permission}' will be ignored")
          false
        }
    }.map { accessRule => GlobalPermission(accessRule.permission) -> accessRule }.toMap
    val undefinedGlobalPermissions = GlobalPermissions.values.filterNot { definedGlobalPermissions contains _ }

    val definedForumPermissions = permissions.defaultForumPermissions.filter {
      accessRule =>
        if (ForumPermissions.valuesByName.contains(accessRule.permission))
          true
        else {
          Logger.warn(s"Unknown default forum permission '${accessRule.permission}' will be ignored")
          false
        }
    }.map { accessRule => ForumPermission(accessRule.permission) -> accessRule }.toMap
    val undefinedForumPermissions = ForumPermissions.values.filterNot { definedForumPermissions contains _ }

    val completeGlobalPermissions = definedGlobalPermissions ++ undefinedGlobalPermissions.map {
      permission =>
        permission -> (permission match {
          case Admin  => makeDefaultAccessRule(permission.name, false, "admin")
          case Forums => makeDefaultAccessRule(permission.name, true)
          case _      => makeDefaultAccessRule(permission.name, false)
        })
    }.toMap

    val completeForumPermissions = definedForumPermissions ++ undefinedForumPermissions.map {
      permission =>
        permission -> (permission match {
          case Access => makeDefaultAccessRule(permission.name, true)
          case Sticky => makeDefaultAccessRule(permission.name, false, Seq("admin", "supermod"))
          case Close  => makeDefaultAccessRule(permission.name, false, Seq("admin", "supermod"))
          case _      => makeDefaultAccessRule(permission.name, false)
        })
    }.toMap

    PermissionConfiguration(
      GlobalPermissions.values.map { completeGlobalPermissions apply _ },
      ForumPermissions.values.map { completeForumPermissions apply _ })

  }

  private def makeDefaultAccessRule(permission: String, guestAllowed: Boolean) =
    AccessRule(permission, None, None, None, None, guestAllowed)

  private def makeDefaultAccessRule(permission: String, guestAllowed: Boolean, permittedGroup: String) =
    AccessRule(permission, None, None, None, Some(Seq(permittedGroup)), guestAllowed)

  private def makeDefaultAccessRule(permission: String, guestAllowed: Boolean, permittedGroups: Seq[String]) =
    AccessRule(permission, None, None, None, Some(permittedGroups), guestAllowed)

  def hasGlobalPermission(permission: GlobalPermission)(implicit maybeUser: Option[User]): Boolean =
    permissions.globalPermissionAllowed(permission).getOrElse(false)

  def getAllowedGlobalPermissions(implicit maybeUser: Option[User]): Seq[GlobalPermission] =
    GlobalPermissions.values.filter { hasGlobalPermission(_) }

  def requireGlobalPermission(permission: GlobalPermission)(implicit maybeUser: Option[User]): Unit =
    if (!hasGlobalPermission(permission)) accessDeniedException

  def requireGlobalPermissions(permissions: GlobalPermission*)(implicit maybeUser: Option[User]): Unit =
    if (!permissions.forall(hasGlobalPermission(_))) accessDeniedException

  def hasForumPermission(permission: ForumPermission, forum: Forum, category: ForumCategory)(implicit maybeUser: Option[User]): Boolean =
    permissions.forumPermissionAllowed(permission, forum, category).getOrElse(false)

  def getAllowedForumPermissions(forum: Forum, category: ForumCategory)(implicit maybeUser: Option[User]): Seq[ForumPermission] =
    ForumPermissions.values.filter { hasForumPermission(_, forum, category) }

  def requireForumPermission(permission: ForumPermission, forum: Forum, category: ForumCategory)(implicit maybeUser: Option[User]): Unit =
    if (!hasForumPermission(permission, forum, category)) accessDeniedException

}