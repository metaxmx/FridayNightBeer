package authentication

import models.{AccessRule, User}
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for access rule authorization.
  */
class AccessRuleAuthorizationTest extends WordSpec with MustMatchers {

  "An empty access rule" should {
    val rule = AccessRule.empty
    "be denied access by anonymous user" in new AnonymousFixture {
      auth.authorize(rule) mustBe false
    }
    "be denied access by authenticated user" in new AuthenticatedFixture {
      auth.authorize(rule) mustBe false
    }
    "be denied access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
      auth.authorize(rule) mustBe false
    }
  }

  "An access rule with 'allowAll'" when {
    "having no excludes" should {
      val rule = AccessRule.empty.withAllowAll
      "be granted access by anonymous user" in new AnonymousFixture {
        auth.authorize(rule) mustBe true
      }
      "be granted access by authenticated user" in new AuthenticatedFixture {
        auth.authorize(rule) mustBe true
      }
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        auth.authorize(rule) mustBe true
      }
    }
    "excluding the user" should {
      "be granted access by authenticated user" in new AuthenticatedFixture {
        val rule = AccessRule.empty.withAllowAll.withForbiddenUsers(userId)
        auth.authorize(rule) mustBe true
      }
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowAll.withForbiddenUsers(userId)
        auth.authorize(rule) mustBe true
      }
    }
    "excluding a group of the user" should {
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowAll.withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe true
      }
    }
  }

  "An access rule with 'allowAllUsers'" when {
    "having no excludes" should {
      val rule = AccessRule.empty.withAllowAllUsers
      "be denied access by anonymous user" in new AnonymousFixture {
        auth.authorize(rule) mustBe false
      }
      "be granted access by authenticated user" in new AuthenticatedFixture {
        auth.authorize(rule) mustBe true
      }
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        auth.authorize(rule) mustBe true
      }
    }
    "excluding the user" should {
      "be denied access by this user" in new AuthenticatedFixture {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
      "be granted access by another user" in new AuthenticatedFixture with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenUsers(userId)
        anotherAuth.authorize(rule) mustBe true
      }
      "be denied access by this user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding a group of the user" should {
      "be granted access by a user without this group" in new AuthenticatedFixtureWithGroups with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenGroups(groupId1)
        anotherAuth.authorize(rule) mustBe true
      }
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding the user and a group of the user" should {
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowAllUsers.withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
  }

  "An access rule allowing a user" when {
    "having no excludes" should {
      "be denied access by anonymous user" in new AnonymousFixture with UserIdFixture {
        val rule = AccessRule.empty.withAllowUsers(userId)
        auth.authorize(rule) mustBe false
      }
      "be granted access by authenticated user" in new AuthenticatedFixture {
        val rule = AccessRule.empty.withAllowUsers(userId)
        auth.authorize(rule) mustBe true
      }
      "be denied access by another user" in new AuthenticatedFixture with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowUsers(userId)
        anotherAuth.authorize(rule) mustBe false
      }
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowUsers(userId)
        auth.authorize(rule) mustBe true
      }
    }
    "excluding the user" should {
      "be denied access by this user" in new AuthenticatedFixture {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
      "be denied access by another user" in new AuthenticatedFixture with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenUsers(userId)
        anotherAuth.authorize(rule) mustBe false
      }
      "be denied access by this user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding a group of the user" should {
      "be denied access by a user without this group" in new AuthenticatedFixtureWithGroups with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenGroups(groupId1)
        anotherAuth.authorize(rule) mustBe false
      }
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding the user and a group of the user" should {
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowUsers(userId).withForbiddenUsers(userId).withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
  }

  "An access rule allowing a group" when {
    "having no excludes" should {
      "be denied access by anonymous user" in new AnonymousFixture with GroupIdFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
      "be denied access by authenticated user" in new AuthenticatedFixture with GroupIdFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
      "be denied access by another user" in new AuthenticatedFixture with AnotherUserFixture with GroupIdFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1)
        anotherAuth.authorize(rule) mustBe false
      }
      "be granted access by authenticated user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowGroups(groupId1)
        auth.authorize(rule) mustBe true
      }
    }
    "excluding the user" should {
      "be denied access by this user" in new AuthenticatedFixture with GroupIdFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
      "be denied access by another user" in new AuthenticatedFixture with AnotherUserFixture with GroupIdFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenUsers(userId)
        anotherAuth.authorize(rule) mustBe false
      }
      "be denied access by this user with groups" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenUsers(userId)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding a group of the user" should {
      "be denied access by a user without this group" in new AuthenticatedFixtureWithGroups with AnotherUserFixture {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenGroups(groupId1)
        anotherAuth.authorize(rule) mustBe false
      }
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding another group of the user" should {
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenGroups(groupId2)
        auth.authorize(rule) mustBe false
      }
    }
    "excluding the user and a group of the user" should {
      "be denied access by a user with this group" in new AuthenticatedFixtureWithGroups {
        val rule = AccessRule.empty.withAllowGroups(groupId1).withForbiddenUsers(userId).withForbiddenGroups(groupId1)
        auth.authorize(rule) mustBe false
      }
    }
  }

  trait UserIdFixture {
    lazy val userId = "xyz"
  }

  trait GroupIdFixture {
    lazy val groupId1 = "abc"
  }

  trait AnonymousFixture {
    val auth: AccessRuleAuthorization = UnauthenticatedProfile
  }

  trait AuthenticatedFixture extends UserIdFixture {
    def groups: Seq[String] = Seq.empty
    val user = User(userId, "user1", "", "User1", "user1@localhost", None, None, Some(groups))
    val auth: AccessRuleAuthorization = AuthenticatedProfile(user)
  }

  trait AuthenticatedFixtureWithGroups extends AuthenticatedFixture with GroupIdFixture {
    lazy val groupId2 = "def"
    override def groups = Seq(groupId1, groupId2)
  }

  trait AnotherUserFixture {
    val anotherUserId = "fff"
    val anotherUser = User(anotherUserId, "user1", "", "User1", "user1@localhost", None, None, None)
    val anotherAuth: AccessRuleAuthorization = AuthenticatedProfile(anotherUser)
  }

}
