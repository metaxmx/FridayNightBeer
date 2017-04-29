package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for access rule chain.
  */
class AccessRuleChainTest extends WordSpec with MustMatchers {

  "An Access Rule Chain" when {
    "empty" should {
      "contain no access rules" in {
        AccessRuleChain.empty.accessRules mustBe empty
      }
      "resolve to the empty Access Rule" in {
        AccessRuleChain.empty.reduceToSingleRule mustBe AccessRule.empty
      }
    }
    "containing one access rule" should {
      "resolve to this Access Rule" in {
        val rule = AccessRule(
          allowAll = None,
          allowAllUsers = Some(false),
          allowedUsers = None,
          allowedGroups = Some(Seq("C")),
          forbiddenUsers = None,
          forbiddenGroups = Some(Seq("Z"))
        )
        AccessRuleChain(Seq(rule)).reduceToSingleRule mustBe rule
      }
    }
    "containing two access rules" should {
      "resolve to the combination of the access rules, with the first rule as lower precedence" in {
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
        val combination = mainRule withFallback fallback

        AccessRuleChain(Seq(fallback, mainRule)).reduceToSingleRule mustBe combination
      }
    }
  }

  "The Access Rule Chain factory" when {
    "called with empty options" should {
      "return the empty access rule chain" in {
        AccessRuleChain(None) mustBe AccessRuleChain.empty
        AccessRuleChain(None, None, None, None) mustBe AccessRuleChain.empty
      }
    }
    "called with a sequence of some empty and some non-empty options" should {
      "return the access rule chain with the non-empty rules" in {
        val rule1 = AccessRule(
          allowAll = Some(false),
          allowAllUsers = Some(true),
          allowedUsers = Some(Seq("D")),
          allowedGroups = Some(Seq("E", "F")),
          forbiddenUsers = Some(Seq("U")),
          forbiddenGroups = Some(Seq("V", "W"))
        )
        val rule2 = AccessRule(
          allowAll = None,
          allowAllUsers = Some(false),
          allowedUsers = None,
          allowedGroups = Some(Seq("C")),
          forbiddenUsers = None,
          forbiddenGroups = Some(Seq("Z"))
        )
        val rule3 = AccessRule(
          allowAll = Some(true),
          allowAllUsers = None,
          allowedUsers = None,
          allowedGroups = Some(Seq("Z")),
          forbiddenUsers = None,
          forbiddenGroups = None
        )

        AccessRuleChain(None, None, Some(rule1), None, Some(rule2), None, Some(rule3)) mustBe
          AccessRuleChain(Seq(rule1, rule2, rule3))
      }
    }
  }

}
