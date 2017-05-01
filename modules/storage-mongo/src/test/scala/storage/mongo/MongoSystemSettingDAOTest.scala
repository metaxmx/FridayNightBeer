package storage.mongo

import java.util.Date

import org.scalatest.MustMatchers
import play.api.cache.CacheApi
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Test for System Settings DAO with MongoDB.
  */
class MongoSystemSettingDAOTest extends MongoDAOTestSpec with MustMatchers {

  def buildDAO(cacheApi: CacheApi, mongoApi: ReactiveMongoApi, suffix: Option[String]): MongoSystemSettingDAO =
    new MongoSystemSettingDAO(cacheApi, mongoApi, suffix)

  val timeout = 10.seconds

  "A System Settings DAO" should {

    "fetch default and store default to db if settings not existing" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "keynotexisting"
        val defaultValue = "DefaultValue"
        val anotherDefaultValue = "Another Default Value"

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe defaultValue
        Await.result(dao.getSetting(key, anotherDefaultValue), timeout) mustBe defaultValue

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, anotherDefaultValue), timeout) mustBe defaultValue
      }
    }

    "inserting system setting if not yet existing" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value = "This is a great value!"
        val defaultValue = "Default value"

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "overwrite system setting if already existing" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value = "This is a great value!"
        val newValue = "This is some new value!"
        val defaultValue = "Default value"

        Await.result(dao.changeSetting(key, value), timeout) mustBe value
        Await.result(dao.changeSetting(key, newValue), timeout) mustBe newValue

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe newValue

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe newValue
      }
    }

    "serialize and deserialize Boolean" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Boolean = true
        val defaultValue: Boolean = false

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "serialize and deserialize Int" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Int = 234
        val defaultValue: Int = 0

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "serialize and deserialize Long" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Long = 1000000000L
        val defaultValue: Long = 0

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "serialize and deserialize Float" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Float = 3.5f
        val defaultValue: Float = 0.0f

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "serialize and deserialize Double" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Double = 34.896745231d
        val defaultValue: Double = 0.0d

        Await.result(dao.changeSetting(key, value), timeout) mustBe value

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value

        // Invalidate cache to see if value was written to underlying database
        dao.invalidateCache()

        Await.result(dao.getSetting(key, defaultValue), timeout) mustBe value
      }
    }

    "fail on serialize of other types" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: Date = new Date()
        val defaultValue: Date = new Date()

        intercept[IllegalArgumentException] {
          Await.result(dao.changeSetting(key, value), timeout)
        }

        intercept[IllegalArgumentException] {
          Await.result(dao.getSetting(key, defaultValue), timeout)
        }
      }
    }

    "fail on deserialize of other types" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val key = "somekey"
        val value: String = "something"
        val expectedValue: Date = new Date()

        Await.ready(dao.changeSetting(key, value), timeout)

        intercept[IllegalArgumentException] {
          Await.result(dao.getSetting(key, expectedValue), timeout)
        }
      }
    }
  }

}
