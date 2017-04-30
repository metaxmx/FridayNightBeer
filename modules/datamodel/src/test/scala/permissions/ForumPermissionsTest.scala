package permissions

import org.scalatest.{MustMatchers, WordSpec}
import ForumPermissions._

/**
  * Test Forum Permissions
  */
class ForumPermissionsTest extends WordSpec with MustMatchers {

  "The forum permissions" should {

    "have to correct type" in {
      name mustBe "ForumPermission"
    }

    "contain the complete list" in {
      values.toSet mustBe Set(Access, CreateThread, Sticky, Close, DeleteThread)
    }

    "contain the complete map" in {
      valuesByName mustBe Map(
        "Access" -> Access,
        "CreateThread" -> CreateThread,
        "Sticky" -> Sticky,
        "Close" -> Close,
        "DeleteThread" -> DeleteThread
      )
    }

    "return the correct value by apply" in {
      ForumPermission("Access") mustBe Access
      ForumPermission("CreateThread") mustBe CreateThread
      ForumPermission("Sticky") mustBe Sticky
      ForumPermission("Close") mustBe Close
      ForumPermission("DeleteThread") mustBe DeleteThread
    }

    "return the correct value by unapply" in {
      ForumPermission.unapply(Access) mustBe Some("Access")
      ForumPermission.unapply(CreateThread) mustBe Some("CreateThread")
      ForumPermission.unapply(Sticky) mustBe Some("Sticky")
      ForumPermission.unapply(Close) mustBe Some("Close")
      ForumPermission.unapply(DeleteThread) mustBe Some("DeleteThread")

      val matched = Close match {
        case ForumPermission(e) => e
      }
      matched mustBe "Close"
    }

  }

}
