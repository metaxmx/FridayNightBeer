package models

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for System Settings Model
  */
class SystemSettingTest extends WordSpec with MustMatchers {

  "A system settings model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val setting = SystemSetting("key", "value")
        val changedSetting = setting.withId(newId)
        changedSetting._id mustBe newId
        changedSetting mustBe setting.copy(_id = newId)
      }
    }
  }

}
