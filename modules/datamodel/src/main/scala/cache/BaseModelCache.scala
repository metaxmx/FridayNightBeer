package cache

import models.BaseModel
import play.api.cache.CacheApi
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import scala.util.Success

/**
  * Cache for entities of a specific [[models.BaseModel]].
  *
  * @param cache      injected play cache
  * @param prefix     prefix for the typed cache (must be unique)
  * @param expiration expiration date for all data put inside the cache
  * @tparam T base model type
  */
class BaseModelCache[T <: BaseModel[T] : ClassTag](cache: CacheApi, prefix: String, expiration: Duration = Duration.Inf) {

  /**
    * Get key from entity id.
    *
    * @param id entity id
    * @return prefix and id
    */
  protected[cache] def key(id: String) = prefix + "/" + id

  /**
    * Get entity from the cache.
    *
    * @param id id to query for
    * @return [[scala.Some]] found entity, or [[scala.None]]
    */
  def get(id: String): Option[T] = cache.get(key(id))

  /**
    * Put entity into cache. The key is derived from the entity.
    *
    * @param entity entity
    */
  def set(entity: T): Unit = {
    cache.set(key(entity._id), entity, expiration)
  }

  /**
    * Remove entity from cache. If entity is not inside cache, nothing happens.
    *
    * @param id entity id to delete
    */
  def remove(id: String): Unit = cache.remove(key(id))

  /**
    * Get entity from the cache. If the queried id is not contained in the cache, the given entity provider is used to
    * get the entity, and the entity is then put inside the cache if successful.
    *
    * @param id    query entity id
    * @param block asynchronous block to fetch the entity by the given id
    * @return [[scala.concurrent.Future]] of the entity (with [[scala.None]], if the entity was not found)
    */
  def getOrElseAsync(id: String, block: => FutureOption[T]): FutureOption[T] =
    FutureOption fromOption get(id) orElse {
      block andThen {
        case Success(Some(result)) => set(result)
      }
    }

  /**
    * Get entity from the cache. If the queried id is not contained in the cache, the given entity provider is used to
    * get the entity, and the entity is then put inside the cache.
    *
    * @param id    query entity id
    * @param block asynchronous block to fetch the entity by the given id
    * @return [[scala.concurrent.Future]] of the entity (with [[scala.None]], if the entity was not found)
    */
  def getOrElseAsyncDef(id: String, block: => Future[T]): Future[T] =
    get(id) map { Future.successful } getOrElse {
      block andThen {
        case Success(result) => set(result)
      }
    }
}