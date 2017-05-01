package storage.mongo

import play.api.cache.CacheApi

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

/**
  * Mock for Cache API to use in-memory structure to simulate cache.
  * Expiration is ignored, as it is not heavily used in the calling DAOs.
  */
class InMemoryCacheApi extends CacheApi {

  private var data: Map[String, Any] = Map.empty

  override def set(key: String, value: Any, expiration: Duration): Unit = synchronized {
    data += key -> value
  }

  override def remove(key: String): Unit = synchronized {
    data -= key
  }

  override def getOrElse[A: ClassTag](key: String, expiration: Duration)(orElse: => A): A = synchronized {
    get(key).getOrElse(orElse)
  }

  override def get[T: ClassTag](key: String): Option[T] = synchronized {
    data.get(key).map(_.asInstanceOf[T])
  }
}
