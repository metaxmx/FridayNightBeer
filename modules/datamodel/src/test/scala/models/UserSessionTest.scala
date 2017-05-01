package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for User Session Model
  */
class UserSessionTest extends WordSpec with MustMatchers {

  "A user session model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val session = UserSession("testId", Some("userId"))
        val changedSession = session.withId(newId)
        changedSession._id mustBe newId
        changedSession mustBe session.copy(_id = newId)
      }
    }
    "calling withUser for None" should {
      "return an identical copy and replace the user with no user" in {
        val session = UserSession("testId", Some("userId"))
        val changedSession = session.withUser(None)
        changedSession.user mustBe None
        changedSession._id mustBe session._id
      }
    }
    "calling withUser for Some user" should {
      "return an identical copy and replace the user with this user" in {
        val userId = "userId"
        val session = UserSession("testId", None)
        val changedSession = session.withUser(Some(userId))
        changedSession.user mustBe Some(userId)
        changedSession._id mustBe session._id
      }
    }
  }

}

