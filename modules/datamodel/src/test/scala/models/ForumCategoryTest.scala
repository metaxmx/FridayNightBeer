package models

import org.scalatest.{MustMatchers, WordSpec}
import permissions.{ForumPermissions, ThreadPermissions}

/**
  * Test for Forum Category Model
  */
class ForumCategoryTest extends WordSpec with MustMatchers {

  "A forum category model" when {
    "having no forum permissions" should {
      "have an empty forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = None
        val cat = ForumCategory("testcat", "Test Category",0, forumPermissions, None)
        cat.forumPermissionMap mustBe empty
      }
    }
    "having empty forum permissions" should {
      "have an empty forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = Some(Map.empty)
        val cat = ForumCategory("testcat", "Test Category",0, forumPermissions, None)
        cat.forumPermissionMap mustBe empty
      }
    }
    "having some forum permissions" should {
      "reflect in forum permission map" in {
        val forumPermissions: Option[Map[String, AccessRule]] = Some(Map(ForumPermissions.Access.name -> AccessRule.empty))
        val cat = ForumCategory("testcat", "Test Category",0, forumPermissions, None)
        cat.forumPermissionMap mustBe Map(ForumPermissions.Access.name -> AccessRule.empty)
      }
    }
    "having no thread permissions" should {
      "have an empty thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = None
        val cat = ForumCategory("testcat", "Test Category",0, None, threadPermissions)
        cat.threadPermissionMap mustBe empty
      }
    }
    "having empty thread permissions" should {
      "have an empty thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map.empty)
        val cat = ForumCategory("testcat", "Test Category",0, None, threadPermissions)
        cat.threadPermissionMap mustBe empty
      }
    }
    "having some thread permissions" should {
      "reflect in thread permission map" in {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map(ThreadPermissions.Access.name -> AccessRule.empty))
        val cat = ForumCategory("testcat", "Test Category",0, None, threadPermissions)
        cat.threadPermissionMap mustBe Map(ThreadPermissions.Access.name -> AccessRule.empty)
      }
    }
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val cat = ForumCategory("testcat", "Test Category",0, None, None)
        val changedCat = cat.withId(newId)
        changedCat._id mustBe newId
        changedCat mustBe cat.copy(_id = newId)

      }
    }
  }

}
