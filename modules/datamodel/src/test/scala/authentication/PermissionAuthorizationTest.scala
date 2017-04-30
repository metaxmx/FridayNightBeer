package authentication

import models._
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import permissions.ForumPermissions.ForumPermission
import permissions.{ForumPermissions, GlobalPermissions}
import storage.PermissionDAO.PermissionMap

/**
  * Test permission authorization
  */
class PermissionAuthorizationTest extends WordSpec with MustMatchers with MockitoSugar {

  "A permission authorization" when {
    "no global permissions are defined" should {
      "list no global permissions" in new EmptyGlobalAuthFixture {
        auth.listGlobalPermissions.toSet mustBe empty
      }
      "deny all global permission checks" in new EmptyGlobalAuthFixture {
        GlobalPermissions.values.foreach {
          globalPermission =>
            auth.checkGlobalPermissions(globalPermission) mustBe false
        }
      }
      "deny a global permission check for all" in new EmptyGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.values: _*) mustBe false
      }
    }

    "all global permissions are denied" should {
      "list no global permissions" in new AllowNoneGlobalAuthFixture {
        auth.listGlobalPermissions.toSet mustBe empty
      }
      "deny all global permission checks" in new AllowNoneGlobalAuthFixture {
        GlobalPermissions.values.foreach {
          globalPermission =>
            auth.checkGlobalPermissions(globalPermission) mustBe false
        }
      }
      "deny a global permission check for all" in new AllowNoneGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.values: _*) mustBe false
      }
    }

    "some global permissions are granted" should {
      "list those global permissions" in new AllowSomeGlobalAuthFixture {
        auth.listGlobalPermissions.toSet mustBe Set(GlobalPermissions.Forums, GlobalPermissions.Admin).map(_.name)
      }
      "grant those global permission checks" in new AllowSomeGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.Admin) mustBe true
        auth.checkGlobalPermissions(GlobalPermissions.Forums) mustBe true
      }
      "deny other global permission checks" in new AllowSomeGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.Media) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Members) mustBe false
      }
      "grant a global permission check for all allowed" in new AllowSomeGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.Admin, GlobalPermissions.Forums) mustBe true
      }
      "deny a global permission check if any other permission is included" in new AllowSomeGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.Admin, GlobalPermissions.Forums, GlobalPermissions.Media) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Media, GlobalPermissions.Admin, GlobalPermissions.Forums) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Media, GlobalPermissions.Forums) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Media, GlobalPermissions.Admin) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Forums, GlobalPermissions.Media) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Admin, GlobalPermissions.Media) mustBe false
        auth.checkGlobalPermissions(GlobalPermissions.Members, GlobalPermissions.Media) mustBe false
      }
    }

    "all global permissions are granted" should {
      "list all global permissions" in new AllowAllGlobalAuthFixture {
        auth.listGlobalPermissions.toSet mustBe GlobalPermissions.values.map(_.name).toSet
      }
      "grant all global permission checks" in new AllowAllGlobalAuthFixture {
        GlobalPermissions.values.foreach {
          globalPermission =>
            auth.checkGlobalPermissions(globalPermission) mustBe true
        }
      }
      "grant a global permission check for all" in new AllowAllGlobalAuthFixture {
        auth.checkGlobalPermissions(GlobalPermissions.values: _*) mustBe true
      }
    }

    "some forum permissions are granted" when {
      "the forum defines no or only positive permissions" should {
        "grant those forum permissions" in new ForumPermissionFixture {
          val (cEmpty, fEmpty) = testForum()
          val (cCP, fCP) = testForumCP(ForumPermissions.Access -> positiveAccessRule)
          val (cFP, fFP) = testForumFP(ForumPermissions.Access -> positiveAccessRule)
          val (cFull, fFull) = testForum(ForumPermissions.Access -> positiveAccessRule,
            ForumPermissions.Access -> positiveAccessRule)

          auth.checkForumPermissions(cEmpty, fEmpty, ForumPermissions.Access) mustBe true
          auth.checkForumPermissions(cCP, fCP, ForumPermissions.Access) mustBe true
          auth.checkForumPermissions(cFP, fFP, ForumPermissions.Access) mustBe true
          auth.checkForumPermissions(cFull, fFull, ForumPermissions.Access) mustBe true
        }
        "deny other forum permissions" in new ForumPermissionFixture {
          val (cEmpty, fEmpty) = testForum()
          val (cCP, fCP) = testForumCP(ForumPermissions.Access -> positiveAccessRule)
          val (cFP, fFP) = testForumFP(ForumPermissions.Access -> positiveAccessRule)
          val (cFull, fFull) = testForum(ForumPermissions.Access -> positiveAccessRule,
            ForumPermissions.Access -> positiveAccessRule)

          auth.checkForumPermissions(cEmpty, fEmpty, ForumPermissions.Sticky) mustBe false
          auth.checkForumPermissions(cCP, fCP, ForumPermissions.Sticky) mustBe false
          auth.checkForumPermissions(cFP, fFP, ForumPermissions.Sticky) mustBe false
          auth.checkForumPermissions(cFull, fFull, ForumPermissions.Sticky) mustBe false
        }
        "list those forum permissions" in new ForumPermissionFixture {
          val (cEmpty, fEmpty) = testForum()
          val (cCP, fCP) = testForumCP(ForumPermissions.Access -> positiveAccessRule)
          val (cFP, fFP) = testForumFP(ForumPermissions.Access -> positiveAccessRule)
          val (cFull, fFull) = testForum(ForumPermissions.Access -> positiveAccessRule,
            ForumPermissions.Access -> positiveAccessRule)

          val expectedPermissions = Set(ForumPermissions.Access.name, ForumPermissions.CreateThread.name)
          auth.listForumPermissions(cEmpty, fEmpty).toSet mustBe expectedPermissions
          auth.listForumPermissions(cCP, fCP).toSet mustBe expectedPermissions
          auth.listForumPermissions(cFP, fFP).toSet mustBe expectedPermissions
          auth.listForumPermissions(cFull, fFull).toSet mustBe expectedPermissions
        }
      }
    }
  }

  trait UserAuthFixture {

    // Test with real user authentication, returning true of accessRule contains userId

    lazy val userId: String = "user1"
    lazy val user: User = User(userId, "testuser", "", "Test User", "testuser@localhost", None, None, None)
    lazy val userAuthentication: AccessRuleAuthorization = AuthenticatedProfile(user)

    lazy val positiveAccessRule: AccessRule = AccessRule.empty.withAllowUsers(userId).withForbiddenUsers()
    lazy val neutralAccessRule: AccessRule = AccessRule.empty
    lazy val negativeAccessRule: AccessRule = AccessRule.empty.withForbiddenUsers(userId).withAllowUsers()

    userAuthentication.authorize(positiveAccessRule) mustBe true
    userAuthentication.authorize(neutralAccessRule) mustBe false
    userAuthentication.authorize(negativeAccessRule) mustBe false

    userAuthentication.authorize(positiveAccessRule overrideWith neutralAccessRule) mustBe true
    userAuthentication.authorize(positiveAccessRule overrideWith negativeAccessRule) mustBe false

    userAuthentication.authorize(neutralAccessRule overrideWith positiveAccessRule) mustBe true
    userAuthentication.authorize(neutralAccessRule overrideWith negativeAccessRule) mustBe false

    userAuthentication.authorize(negativeAccessRule overrideWith positiveAccessRule) mustBe true
    userAuthentication.authorize(negativeAccessRule overrideWith neutralAccessRule) mustBe false

  }

  trait PermissionAuthFixture extends UserAuthFixture {

    def permissions: PermissionMap

    val auth: PermissionAuthorization = new PermissionAuthorization(userAuthentication, permissions)

  }

  trait EmptyGlobalAuthFixture extends PermissionAuthFixture {

    lazy val emptyPermissionMap: PermissionMap = Map.empty

    def permissions = emptyPermissionMap

  }

  trait AllowNoneGlobalAuthFixture extends PermissionAuthFixture {

    lazy val allGlobalPermissionMap: PermissionMap =
      Map(GlobalPermissions.name -> GlobalPermissions.values.map(p => (p.name, negativeAccessRule)).toMap)

    def permissions = allGlobalPermissionMap

  }

  trait AllowSomeGlobalAuthFixture extends PermissionAuthFixture {

    lazy val globalPermissionMap: PermissionMap =
      Map(GlobalPermissions.name -> GlobalPermissions.values.map {
        case p @ GlobalPermissions.Admin => (p.name, positiveAccessRule)
        case p @ GlobalPermissions.Forums => (p.name, positiveAccessRule)
        case p => (p.name, negativeAccessRule)
      }.toMap)

    def permissions = globalPermissionMap

  }

  trait AllowAllGlobalAuthFixture extends PermissionAuthFixture {

    lazy val allGlobalPermissionMap: PermissionMap =
      Map(GlobalPermissions.name -> GlobalPermissions.values.map(p => (p.name, positiveAccessRule)).toMap)

    def permissions = allGlobalPermissionMap

  }

  trait ForumPermissionFixture extends PermissionAuthFixture {

    lazy val forumPermissionMap: PermissionMap =
      Map(ForumPermissions.name -> ForumPermissions.values.map {
        case p @ ForumPermissions.Access => (p.name, positiveAccessRule)
        case p @ ForumPermissions.CreateThread => (p.name, positiveAccessRule)
        case p => (p.name, negativeAccessRule)
      }.toMap)

    def permissions = forumPermissionMap

    def testForum(): (ForumCategory, Forum) = {
      testForum(None, None)
    }

    def testForumCP(catPermission: (ForumPermission, AccessRule)): (ForumCategory, Forum) = {
      testForum(Some(Map(catPermission._1.name -> catPermission._2)), None)
    }

    def testForumFP(forumPermission: (ForumPermission, AccessRule)): (ForumCategory, Forum) = {
      testForum(None, Some(Map(forumPermission._1.name -> forumPermission._2)))
    }

    def testForum(catPermission: (ForumPermission, AccessRule),
                 forumPermission: (ForumPermission, AccessRule)): (ForumCategory, Forum) = {
      testForum(Some(Map(catPermission._1.name -> catPermission._2)),
        Some(Map(forumPermission._1.name -> forumPermission._2)))
    }

    def testForum(catPermissions: Option[Map[String, AccessRule]],
                  forumPermissions: Option[Map[String, AccessRule]]): (ForumCategory, Forum) = {
      val cat = ForumCategory("testCat", "Test Category", 0, catPermissions, None)
      val forum = Forum("testForum", "test Forum", None, None, cat._id, 0, readonly = false, forumPermissions, None)
      (cat, forum)
    }

  }

}
