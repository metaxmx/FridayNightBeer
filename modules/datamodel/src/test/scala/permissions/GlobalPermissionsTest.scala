package permissions

import org.scalatest.{MustMatchers, WordSpec}
import permissions.GlobalPermissions._

/**
  * Test Global Permissions
  */
class GlobalPermissionsTest extends WordSpec with MustMatchers {

  "The global permissions" should {

    "have to correct type" in {
      name mustBe "GlobalPermission"
    }

    "contain the complete list" in {
      values.toSet mustBe Set(Admin, Forums, Events, Media, Members)
    }

    "contain the complete map" in {
      valuesByName mustBe Map(
        "Admin" -> Admin,
        "Forums" -> Forums,
        "Events" -> Events,
        "Media" -> Media,
        "Members" -> Members
      )
    }

    "return the correct value by apply" in {
      GlobalPermission("Admin") mustBe Admin
      GlobalPermission("Forums") mustBe Forums
      GlobalPermission("Events") mustBe Events
      GlobalPermission("Media") mustBe Media
      GlobalPermission("Members") mustBe Members
    }

    "return the correct value by unapply" in {
      GlobalPermission.unapply(Admin) mustBe Some("Admin")
      GlobalPermission.unapply(Forums) mustBe Some("Forums")
      GlobalPermission.unapply(Events) mustBe Some("Events")
      GlobalPermission.unapply(Media) mustBe Some("Media")
      GlobalPermission.unapply(Members) mustBe Some("Members")

      val matched = Events match {
        case GlobalPermission(e) => e
      }
      matched mustBe "Events"
    }

  }

}
