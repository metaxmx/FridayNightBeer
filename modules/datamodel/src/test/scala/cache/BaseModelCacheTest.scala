package cache

import models.BaseModel
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.cache.CacheApi

import scala.concurrent.duration.Duration

/**
  * Test for Base Model Cache
  */
class BaseModelCacheTest extends WordSpec with MustMatchers with MockitoSugar {

  import BaseModelCacheTest._

  "A Base Model Cache with Prefix" should {
    "combine prefix and id for cache key" in {
      val prefix = "pref"
      val testId = "xyz"
      val expectedKey = s"$prefix/$testId"
      val modelCache = new BaseModelCache[TestModel](mock[CacheApi], prefix)
      modelCache.key(testId) mustBe expectedKey
    }
  }

  "A BaseModel Cache" when {
    "value not contained in cache" should {
      "return nothing on get" in new TestModelCacheFixture {
        val cacheId = modelCache.key(testModelId)
        when(cacheApiMock.get[TestModel](cacheId)).thenReturn(None)
        val result = modelCache.get(testModelId)
        verify(cacheApiMock).get[TestModel](cacheId)
        result mustBe empty

      }
    }
  }

  trait TestModelFixture {

    val testModelId = "jdoe"
    val testModel = TestModel(testModelId, "John", "Doe")

    val testModelId2 = "jsmith"
    val testModel2 = TestModel(testModelId2, "Jack", "Smith")

  }

  trait ModelCacheFixture {

    def duration: Duration = Duration.Inf

    val cacheApiMock = mock[CacheApi]

    val modelCache = new BaseModelCache[TestModel](cacheApiMock, "test", duration)

  }

  trait TestModelCacheFixture extends ModelCacheFixture with TestModelFixture {

    def testCacheId = modelCache.key(testModelId)

    def testCacheId2 = modelCache.key(testModelId2)

  }

}

object BaseModelCacheTest {

  case class TestModel(_id: String, firstName: String, lastName: String) extends BaseModel[TestModel] {
    override def withId(id: String): TestModel = copy(_id = id)
  }

}
