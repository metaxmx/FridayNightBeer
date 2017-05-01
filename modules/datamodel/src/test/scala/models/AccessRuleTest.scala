package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test access rule.
  */
class AccessRuleTest extends WordSpec with MustMatchers {

  "An empty access rule" should {
    "not allow access to all" in {
      AccessRule.empty.isAllowAll mustBe false
    }
    "not allow access to all users" in {
      AccessRule.empty.isAllowAllUsers mustBe false
    }
    "not allow access to any users or groups" in {
      AccessRule.empty.allowedUserSet mustBe empty
      AccessRule.empty.allowedGroupSet mustBe empty
    }
    "not deny access to any users or groups" in {
      AccessRule.empty.forbiddenUserSet mustBe empty
      AccessRule.empty.forbiddenGroupSet mustBe empty
    }
  }

  "An access rule" when {
    "copied by the with* functions" should {
      "work for withAllowAll" in {
        val rule = AccessRule.empty.withAllowAll
        rule.isAllowAll mustBe true
        rule.isAllowAllUsers mustBe false
        rule.allowedUserSet mustBe empty
        rule.allowedGroupSet mustBe empty
        rule.forbiddenUserSet mustBe empty
        rule.forbiddenGroupSet mustBe empty
      }
      "work for withAllowAllUsers" in {
        val rule = AccessRule.empty.withAllowAllUsers
        rule.isAllowAll mustBe false
        rule.isAllowAllUsers mustBe true
        rule.allowedUserSet mustBe empty
        rule.allowedGroupSet mustBe empty
        rule.forbiddenUserSet mustBe empty
        rule.forbiddenGroupSet mustBe empty
      }
      "work for withAllowGroups" in {
        val rule = AccessRule.empty.withAllowGroups("group1", "group2")
        rule.isAllowAll mustBe false
        rule.isAllowAllUsers mustBe false
        rule.allowedUserSet mustBe empty
        rule.allowedGroupSet mustBe Set("group1", "group2")
        rule.forbiddenUserSet mustBe empty
        rule.forbiddenGroupSet mustBe empty
      }
      "work for withDenyAll" in {
        val rule = AccessRule.empty.withAllowAll.withDenyAll
        rule.isAllowAll mustBe false
        rule.isAllowAllUsers mustBe false
        rule.allowedUserSet mustBe empty
        rule.allowedGroupSet mustBe empty
        rule.forbiddenUserSet mustBe empty
        rule.forbiddenGroupSet mustBe empty
      }
      "work for withDenyAllUsers" in {
        val rule = AccessRule.empty.withAllowAllUsers.withDenyAllUsers
        rule.isAllowAll mustBe false
        rule.isAllowAllUsers mustBe false
        rule.allowedUserSet mustBe empty
        rule.allowedGroupSet mustBe empty
        rule.forbiddenUserSet mustBe empty
        rule.forbiddenGroupSet mustBe empty
      }
    }
  }

  "An Access Rule with Fallback" when {
    "the base rule has defined properties" should {
      "ignore the properties of the fallback and return the properties of the main rule" in {
        val mainRule = AccessRule(
          allowAll = Some(true),
          allowAllUsers = Some(false),
          allowedUsers = Some(Seq("A", "B")),
          allowedGroups = Some(Seq("C")),
          forbiddenUsers = Some(Seq("X", "Y")),
          forbiddenGroups = Some(Seq("Z"))
        )
        val fallback = AccessRule(
          allowAll = Some(false),
          allowAllUsers = Some(true),
          allowedUsers = Some(Seq("D")),
          allowedGroups = Some(Seq("E", "F")),
          forbiddenUsers = Some(Seq("U")),
          forbiddenGroups = Some(Seq("V", "W"))
        )
        val rule = mainRule withFallback fallback

        rule.isAllowAll mustBe true
        rule.isAllowAllUsers mustBe false
        rule.allowedUserSet mustBe Set("A", "B")
        rule.allowedGroupSet mustBe Set("C")
        rule.forbiddenUserSet mustBe Set("X", "Y")
        rule.forbiddenGroupSet mustBe Set("Z")
      }
    }
    "the base rule has undefined properties" should {
      "return the properties of the fallback rule" in {
        val mainRule = AccessRule.empty
        val fallback = AccessRule(
          allowAll = Some(false),
          allowAllUsers = Some(true),
          allowedUsers = Some(Seq("D")),
          allowedGroups = Some(Seq("E", "F")),
          forbiddenUsers = Some(Seq("U")),
          forbiddenGroups = Some(Seq("V", "W"))
        )
        val rule = mainRule withFallback fallback

        rule.isAllowAll mustBe false
        rule.isAllowAllUsers mustBe true
        rule.allowedUserSet mustBe Set("D")
        rule.allowedGroupSet mustBe Set("E", "F")
        rule.forbiddenUserSet mustBe Set("U")
        rule.forbiddenGroupSet mustBe Set("V", "W")
      }
    }
    "the fallback is empty" should {
      "be equal the original rule" in {
        val mainRule = AccessRule(
          allowAll = None,
          allowAllUsers = Some(false),
          allowedUsers = None,
          allowedGroups = Some(Seq("C")),
          forbiddenUsers = None,
          forbiddenGroups = Some(Seq("Z"))
        )
        val fallback = AccessRule.empty
        val rule = mainRule withFallback fallback

        rule.allowAll mustBe None
        rule.allowAllUsers mustBe Some(false)
        rule.allowedUsers mustBe empty
        rule.allowedGroups mustBe Some(Seq("C"))
        rule.forbiddenUsers mustBe empty
        rule.forbiddenGroups mustBe Some(Seq("Z"))
      }
    }
    "the main rule contains some empty properties" should {
      "be the combination of the original rule and the fallback" in {
        val mainRule = AccessRule(
          allowAll = None,
          allowAllUsers = Some(false),
          allowedUsers = None,
          allowedGroups = Some(Seq("C")),
          forbiddenUsers = None,
          forbiddenGroups = Some(Seq("Z"))
        )
        val fallback = AccessRule(
          allowAll = Some(false),
          allowAllUsers = Some(true),
          allowedUsers = Some(Seq("D")),
          allowedGroups = Some(Seq("E", "F")),
          forbiddenUsers = Some(Seq("U")),
          forbiddenGroups = Some(Seq("V", "W"))
        )
        val rule = mainRule withFallback fallback

        rule.allowAll mustBe Some(false)
        rule.allowAllUsers mustBe Some(false)
        rule.allowedUsers mustBe Some(Seq("D"))
        rule.allowedGroups mustBe Some(Seq("C"))
        rule.forbiddenUsers mustBe Some(Seq("U"))
        rule.forbiddenGroups mustBe Some(Seq("Z"))
      }
    }
  }

  "An Access Rule with Override" should {
    "be the same than the second rule with the first rule as fallback" in {
      val mainRule = AccessRule(
        allowAll = None,
        allowAllUsers = Some(false),
        allowedUsers = None,
        allowedGroups = Some(Seq("C")),
        forbiddenUsers = None,
        forbiddenGroups = Some(Seq("Z"))
      )
      val fallback = AccessRule(
        allowAll = Some(false),
        allowAllUsers = Some(true),
        allowedUsers = Some(Seq("D")),
        allowedGroups = Some(Seq("E", "F")),
        forbiddenUsers = Some(Seq("U")),
        forbiddenGroups = Some(Seq("V", "W"))
      )
      val rule1 = mainRule withFallback fallback
      val rule2 = fallback overrideWith mainRule

      rule1 mustBe rule2
    }
  }

}
