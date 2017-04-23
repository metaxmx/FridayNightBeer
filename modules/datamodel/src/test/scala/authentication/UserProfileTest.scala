package authentication

import models.{Group, User}
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Tests for user profile
  */
class UserProfileTest extends WordSpec with MustMatchers {

  "An unauthenticated profile" should {
    "be not authenticated" in {
      UnauthenticatedProfile.authenticated mustBe false
    }
    "not contain a user" in {
      UnauthenticatedProfile.userOpt mustBe None
      UnauthenticatedProfile.userIdOpt mustBe None
    }
    "have no groups" in {
      UnauthenticatedProfile.groups mustBe 'empty
    }
  }
  "An authenticated profile" when {
    "having no groups" should {
      "be authenticated" in new AuthenticatedProfileFixture {
        profile.authenticated mustBe true
      }
      "contain a user" in new AuthenticatedProfileFixture {
        profile.userOpt mustBe 'defined
        profile.userOpt.get.username mustBe "dummy"
      }
      "contain the correct user id" in new AuthenticatedProfileFixture {
        profile.userIdOpt mustBe 'defined
        profile.userIdOpt mustBe Some(userId)
      }
      "have no groups" in new AuthenticatedProfileFixture {
        profile.groups mustBe 'empty
      }
    }
    "having some groups" should {
      "be authenticated" in new AuthenticatedProfileGroupFixture {
        profile.authenticated mustBe true
      }
      "contain a user" in new AuthenticatedProfileGroupFixture {
        profile.userOpt mustBe 'defined
        profile.userOpt.get.username mustBe "dummy"
      }
      "contain the correct user id" in new AuthenticatedProfileGroupFixture {
        profile.userIdOpt mustBe 'defined
        profile.userIdOpt mustBe Some(userId)
      }
      "contain the groups" in new AuthenticatedProfileGroupFixture {
        profile.groups.size mustBe groups.size
        profile.groups mustBe groups
      }
    }
  }
  "UserProfile factory" must {
    "construct unauthenticated profile from empty parameters" in {
      UserProfile().authenticated mustBe false
      UserProfile(None).authenticated mustBe false
    }
    "construct authenticated profile from user and no groups" in new AuthenticatedProfileFixture {
      UserProfile(user).authenticated mustBe true
      UserProfile(Some(user)).authenticated mustBe true
    }
    "construct authenticated profile from user and groups" in new AuthenticatedProfileGroupFixture {
      UserProfile(user).authenticated mustBe true
      UserProfile(Some(user)).authenticated mustBe true
    }
  }

  trait AuthenticatedProfileFixture {
    val userId = "123456789"
    val user = User(userId, "dummy", "", "dummy", "dummy@dummy.de", None, None, None)
    val profile = AuthenticatedProfile(user)
  }

  trait AuthenticatedProfileGroupFixture {
    val group1Id = "999888777"
    val group2Id = "666555444"
    val userId = "123456789"
    val user = User(userId, "dummy", "", "dummy", "dummy@dummy.de", None, None, Some(Seq(group1Id, group2Id)))
    val groups = Set(group1Id, group2Id)
    val profile = AuthenticatedProfile(user)
  }

}
