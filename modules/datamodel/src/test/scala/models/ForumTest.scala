package models

import authentication.PermissionAuthorization
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import permissions.{ForumPermissions, GlobalPermissions, ThreadPermissions}

/**
  * Test for forum model
  */
class ForumTest extends WordSpec with MustMatchers with MockitoSugar {

  "A forum model" when {
    "having no forum permissions" should {
      "have an empty forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = None
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, forumPermissions, None)
        forum.forumPermissionMap mustBe empty
      }
    }
    "having empty forum permissions" should {
      "have an empty forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = Some(Map.empty)
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, forumPermissions, None)
        forum.forumPermissionMap mustBe empty
      }
    }
    "having some forum permissions" should {
      "reflect in forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = Some(Map(ForumPermissions.Access.name -> AccessRule.empty))
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, forumPermissions, None)
        forum.forumPermissionMap mustBe Map(ForumPermissions.Access.name -> AccessRule.empty)
      }
    }
    "having no thread permissions" should {
      "have an empty thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = None
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, threadPermissions)
        forum.threadPermissionMap mustBe empty
      }
    }
    "having empty thread permissions" should {
      "have an empty thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map.empty)
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, threadPermissions)
        forum.threadPermissionMap mustBe empty
      }
    }
    "having some thread permissions" should {
      "reflect in thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map(ThreadPermissions.Access.name -> AccessRule.empty))
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, threadPermissions)
        forum.threadPermissionMap mustBe Map(ThreadPermissions.Access.name -> AccessRule.empty)
      }
    }
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)
        val changedForum = forum.withId(newId)
        changedForum._id mustBe newId
        changedForum mustBe forum.copy(_id = newId)

      }
    }
    "calling checkAccess" when {
      "global access denied and forum access denied" should {
        "deny access" in {
          val auth = mock[PermissionAuthorization]
          val category = ForumCategory("testcat", "Test Category", 0, None, None)
          val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)

          when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(false)
          when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(false)

          forum.checkAccess(category)(auth) mustBe false
          forum.checkAccess(auth, category) mustBe false

          verify(auth, never()).checkGlobalPermissions(GlobalPermissions.Forums)
          verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
        }
      }
      "global access denied and forum access allowed" should {
        "deny access" in {
          val auth = mock[PermissionAuthorization]
          val category = ForumCategory("testcat", "Test Category", 0, None, None)
          val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)

          when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(false)
          when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(true)

          forum.checkAccess(category)(auth) mustBe false
          forum.checkAccess(auth, category) mustBe false

          verify(auth, times(2)).checkGlobalPermissions(GlobalPermissions.Forums)
          verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
        }
      }
      "global access allowed and forum access denied" should {
        "deny access" in {
          val auth = mock[PermissionAuthorization]
          val category = ForumCategory("testcat", "Test Category", 0, None, None)
          val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)

          when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(true)
          when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(false)

          forum.checkAccess(category)(auth) mustBe false
          forum.checkAccess(auth, category) mustBe false

          verify(auth, never()).checkGlobalPermissions(GlobalPermissions.Forums)
          verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
        }
      }
      "global access allowed and forum access allowed" should {
        "allow access" in {
          val auth = mock[PermissionAuthorization]
          val category = ForumCategory("testcat", "Test Category", 0, None, None)
          val forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)

          when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(true)
          when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(true)

          forum.checkAccess(category)(auth) mustBe true
          forum.checkAccess(auth, category) mustBe true

          verify(auth, times(2)).checkGlobalPermissions(GlobalPermissions.Forums)
          verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
        }
      }
    }
  }

}
