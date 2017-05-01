package models

import org.scalatest.{MustMatchers, WordSpec}
import permissions.GlobalPermissions

/**
  * Test for Permission Model
  */
class PermissionTest extends WordSpec with MustMatchers {

  "A permission model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val permission = Permission("testperm", GlobalPermissions.name, GlobalPermissions.Forums.name, AccessRule.empty)
        val changedPermission = permission.withId(newId)
        changedPermission._id mustBe newId
        changedPermission mustBe permission.copy(_id = newId)
      }
    }
  }

}
