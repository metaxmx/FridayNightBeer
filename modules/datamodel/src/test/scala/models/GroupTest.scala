package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for Group Model
  */
class GroupTest extends WordSpec with MustMatchers {

  "A group model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val group = Group("testgroup", "TestGroup", "Test Group")
        val changedGroup = group.withId(newId)
        changedGroup._id mustBe newId
        changedGroup mustBe group.copy(_id = newId)
      }
    }
  }

}
