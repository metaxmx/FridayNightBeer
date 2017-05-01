package cache

import models.BaseModel
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import play.api.cache.CacheApi
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Test for Base Model Cache
  */
class BaseModelCacheTest extends WordSpec with MustMatchers with MockitoSugar {

  import BaseModelCacheTest._

  val timeout: Duration = 1.second

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
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(None)
        val result = modelCache.get(testModelId)
        verify(cacheApiMock).get[TestModel](testCacheId)
        result mustBe empty
      }
      "execute async future block on getOrElseAsync and insert if non-empty" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId2)).thenReturn(None)
        val futureOpt = FutureOption.fromFuture(Future.successful(testModel2))
        val futureResult = modelCache.getOrElseAsync(testModelId2, futureOpt)
        val result = Await.result(futureResult, timeout)
        verify(cacheApiMock).get[TestModel](testCacheId2)
        result mustBe Some(testModel2)
        verify(cacheApiMock).set(testCacheId2, testModel2, duration)
      }
      "execute async future block on getOrElseAsync and don't insert if empty" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId2)).thenReturn(None)
        val futureOpt = FutureOption()
        val futureResult = modelCache.getOrElseAsync(testModelId2, futureOpt)
        val result = Await.result(futureResult, timeout)
        verify(cacheApiMock).get[TestModel](testCacheId2)
        result mustBe None
        verify(cacheApiMock, never()).set(any(), any(), any())
      }
      "execute async future block on getOrElseAsync and don't insert if future failed" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId2)).thenReturn(None)
        val futureOpt = FutureOption.fromFuture(Future.failed(new IllegalStateException()))
        val futureResult = modelCache.getOrElseAsync(testModelId2, futureOpt)
        intercept[IllegalStateException] {
          Await.result(futureResult, timeout)
        }
        verify(cacheApiMock).get[TestModel](testCacheId2)
        verify(cacheApiMock, never()).set(any(), any(), any())
      }
      "execute async future block on getOrElseAsyncDef and insert" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(None)
        val future = Future.successful(testModel)
        val futureResult = modelCache.getOrElseAsyncDef(testModelId, future)
        val result = Await.result(futureResult, timeout)
        verify(cacheApiMock).get[TestModel](testCacheId)
        result mustBe testModel
        verify(cacheApiMock).set(testCacheId, testModel, duration)
      }
      "execute async future block on getOrElseAsyncDef and don't insert if future failed" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(None)
        val future = Future.failed(new IllegalStateException())
        val futureResult = modelCache.getOrElseAsyncDef(testModelId, future)
        intercept[IllegalStateException] {
          Await.result(futureResult, timeout)
        }
        verify(cacheApiMock).get[TestModel](testCacheId)
        verify(cacheApiMock, never()).set(any(), any(), any())
      }
    }
    "value contained in the cache" should {
      "return the value on get" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(Some(testModel))
        val result = modelCache.get(testModelId)
        verify(cacheApiMock).get[TestModel](testCacheId)
        result mustBe Some(testModel)
      }
      "directly return value on getOrElseAsync" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(Some(testModel))
        val failedFutureOpt = FutureOption.fromFuture(Future.failed(new IllegalStateException()))
        val futureResult = modelCache.getOrElseAsync(testModelId, failedFutureOpt)
        val result = Await.result(futureResult, timeout)
        verify(cacheApiMock).get[TestModel](testCacheId)
        result mustBe Some(testModel)
      }
      "directly return value on getOrElseAsyncDef" in new TestModelCacheFixture {
        when(cacheApiMock.get[TestModel](testCacheId)).thenReturn(Some(testModel))
        val failedFuture = Future.failed(new IllegalStateException())
        val futureResult = modelCache.getOrElseAsyncDef(testModelId, failedFuture)
        val result = Await.result(futureResult, timeout)
        verify(cacheApiMock).get[TestModel](testCacheId)
        result mustBe testModel
      }
    }
    "putting value to cache" should {
      "store in cache api" in new TestModelCacheFixture with CaptureSetFixture {
        modelCache.set(testModel)
        val (insertedKey, insertedModel, insertedDuration) = captureInsertedValue()
        insertedKey mustBe testCacheId
        insertedModel mustBe testModel
        insertedDuration mustBe duration
      }
    }
    "removing value from cache" should {
      "remove value from cache-api" in new TestModelCacheFixture{
        modelCache.remove(testModelId)
        verify(cacheApiMock).remove(testCacheId)
      }
    }
  }

  trait TestModelFixture {

    val testModelId: String = "jdoe"
    val testModel: TestModel = TestModel(testModelId, "John", "Doe")

    val testModelId2: String = "jsmith"
    val testModel2: TestModel = TestModel(testModelId2, "Jack", "Smith")

  }

  trait ModelCacheFixture {

    def duration: Duration = Duration.Inf

    val cacheApiMock = mock[CacheApi]

    val modelCache = new BaseModelCache[TestModel](cacheApiMock, "test", duration)

  }

  trait TestModelCacheFixture extends ModelCacheFixture with TestModelFixture {

    def testCacheId: String = modelCache.key(testModelId)

    def testCacheId2: String = modelCache.key(testModelId2)

  }

  trait CaptureSetFixture {
    self: ModelCacheFixture =>

    def captureInsertedValue(): (String, TestModel, Duration) = {
      val keyCaptor = ArgumentCaptor.forClass(classOf[String])
      val modelCaptor = ArgumentCaptor.forClass(classOf[TestModel])
      val durationCaptor = ArgumentCaptor.forClass(classOf[Duration])
      verify(cacheApiMock).set(keyCaptor.capture(), modelCaptor.capture(), durationCaptor.capture())
      (keyCaptor.getValue, modelCaptor.getValue, durationCaptor.getValue)
    }

  }

}

object BaseModelCacheTest {

  case class TestModel(_id: String, firstName: String, lastName: String) extends BaseModel[TestModel] {
    override def withId(id: String): TestModel = copy(_id = id)
  }

}
