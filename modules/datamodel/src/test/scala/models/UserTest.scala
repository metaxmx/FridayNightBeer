package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for User Model
  */
class UserTest extends WordSpec with MustMatchers {

  "A user model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val user = User("testuser", "username", "pw", "firstName", "lastName", None, None, None)
        val changedUser = user.withId(newId)
        changedUser._id mustBe newId
        changedUser mustBe user.copy(_id = newId)
      }
    }
  }

}

