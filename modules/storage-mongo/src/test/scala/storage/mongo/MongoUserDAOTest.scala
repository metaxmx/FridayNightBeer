package storage.mongo

import models.User
import org.scalatest.{MustMatchers, WordSpec}
import play.api.cache.CacheApi
import play.modules.reactivemongo.ReactiveMongoApi
import storage.StorageException

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Test for User DAO with MongoDB.
  */
class MongoUserDAOTest extends MongoDAOTestSpec with MustMatchers {

  def buildDAO(cacheApi: CacheApi, mongoApi: ReactiveMongoApi, suffix: Option[String]): MongoUserDAO =
    new MongoUserDAO(cacheApi, mongoApi, suffix)

  val timeout = 10.seconds

  "A User DAO" should {

    "return empty option if fetched user is not in db" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val id: String = "testuser"
        Await.result(dao.getById(id), timeout) mustBe None
      }
    }

    "insert new user in db and return inserted id" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val user: User = User("", "testUser", "pw", "TestUser", "test@localhost", Some("Test User"), None, Some(Seq("a", "b")))
        val insertedUser = Await.result(dao.insert(user), timeout)
        insertedUser._id mustNot be (user._id)
        val userId = insertedUser._id

        // Fetch again
        val fetchedUser = Await.result(dao.getById(userId), timeout)
        fetchedUser mustBe Some(insertedUser)

        // Fetch again without cache
        dao.invalidateCache()
        val fetchedUser2 = Await.result(dao.getById(userId), timeout)
        fetchedUser2 mustBe Some(insertedUser)
      }
    }

    "list all users" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val user1: User = User("", "testUser", "pw", "TestUser", "test@localhost", Some("Test User"), None, Some(Seq("a", "b")))
        val user2: User = User("", "foo", "pw2", "Foo", "foo@localhost", None, None, None)
        val user3: User = User("", "bar", "pw3", "Bar", "bar@localhost", None, None, Some(Seq("c")))

        val insertedUser1 = Await.result(dao.insert(user1), timeout)
        val insertedUser2 = Await.result(dao.insert(user2), timeout)
        val insertedUser3 = Await.result(dao.insert(user3), timeout)

        insertedUser1 mustBe user1.copy(_id = insertedUser1._id)
        insertedUser2 mustBe user2.copy(_id = insertedUser2._id)
        insertedUser3 mustBe user3.copy(_id = insertedUser3._id)

        val allUsers = Await.result(dao.getAll, timeout)
        allUsers.toSet mustBe Set(insertedUser1, insertedUser2, insertedUser3)

        val allUsersById = Await.result(dao.getMap, timeout)
        allUsersById mustBe Set(insertedUser1, insertedUser2, insertedUser3).map(u => (u._id, u)).toMap
      }
    }

    "raise error is inserted user already exists" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val user: User = User("", "testUser", "pw", "TestUser", "test@localhost", Some("Test User"), None, Some(Seq("a", "b")))
        val insertedUser = Await.result(dao.insert(user), timeout)
        intercept[StorageException] {
          Await.result(dao.insertWithGivenId(insertedUser), timeout)
        }
      }
    }

    "delete a user" in new MongoFixture {
      withMongoDatabase(buildDAO) { dao =>
        val user1: User = User("", "testUser", "pw", "TestUser", "test@localhost", Some("Test User"), None, Some(Seq("a", "b")))
        val user2: User = User("", "foo", "pw2", "Foo", "foo@localhost", None, None, None)
        val user3: User = User("", "bar", "pw3", "Bar", "bar@localhost", None, None, Some(Seq("c")))

        val insertedUser1 = Await.result(dao.insert(user1), timeout)
        val insertedUser2 = Await.result(dao.insert(user2), timeout)
        val insertedUser3 = Await.result(dao.insert(user3), timeout)

        Await.result(dao.remove(insertedUser1), timeout) mustBe true
        Await.result(dao.remove(insertedUser2._id), timeout) mustBe true
        Await.result(dao.remove("thisIdDoesNotExist"), timeout) mustBe false

        val allUserIds: Set[String] = Await.result(dao.map(_.map(_._id).toSet), timeout)
        allUserIds mustBe Set(insertedUser3._id)

        // Fetch again without cache
        dao.invalidateCache()
        val allUserIds2: Set[String] = Await.result(dao.map(_.map(_._id).toSet), timeout)
        allUserIds2 mustBe Set(insertedUser3._id)
      }
    }
  }

}
