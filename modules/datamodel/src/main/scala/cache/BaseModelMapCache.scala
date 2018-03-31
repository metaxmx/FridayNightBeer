package cache

import models.BaseModel
import play.api.cache.SyncCacheApi
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Success

/**
  * Cache for all entities of a specific [[models.BaseModel]], indexed by id.
  *
  * @param cache      injected play cache
  * @param prefix     prefix for the typed cache (must be unique)
  * @param expiration expiration date for all data put inside the cache
  * @tparam T base model type
  */
class BaseModelMapCache[T <: BaseModel[T]](cache: SyncCacheApi,
                                           prefix: String,
                                           expiration: Duration = Duration.Inf) {

  type ModelMap = Map[String, T]

  /**
    * Key for model map.
    */
  private[this] val key = prefix + "/$all"

  /**
    * Get whole model map.
    *
    * @return Model map or None if not loaded
    */
  def getAll: Option[ModelMap] = cache.get(key)

  /**
    * Get entity from the cache.
    *
    * @param id id to query for
    * @return [[scala.Some]] found entity, or [[scala.None]]
    */
  def get(id: String): Option[T] = getAll flatMap (_.get(id))

  /**
    * Replace entity in the model map. If the model map is not loaded, nothing happens.
    *
    * @param id     id to update
    * @param entity [[scala.Some]] entity or [[scala.None]]
    * @return true if update was successful
    */
  private[this] def replaceEntry(id: String, entity: Option[T]): Boolean = getAll exists {
    map =>
      setAll(entity match {
        case None => map - id
        case Some(newEntity) => map + (id -> newEntity)
      })
      true
  }

  /**
    * Set whole model map.
    *
    * @param models Model map
    */
  def setAll(models: ModelMap): Unit = cache.set(key, models, expiration)

  /**
    * Put entity into cache. The key is derived from the entity.
    *
    * @param entity entity
    */
  def set(entity: T): Unit = replaceEntry(entity._id, Some(entity))

  /**
    * Remove entity from cache. If entity is not inside cache, nothing happens.
    *
    * @param id entity id to delete
    */
  def remove(id: String): Unit = replaceEntry(id, None)

  /**
    * Remove whole map from cache.
    */
  def removeAll(): Unit = cache.remove(key)

  /**
    * Get whole model map from cache. If cache is empty, the given entity collection provider is used to
    * fetch the data, and the data is then put inside the cache if successful.
    *
    * @param block asynchronous block to fetch all entities
    * @tparam A type of returned entity collection
    * @return [[scala.concurrent.Future]] of the model map
    */
  def getAllOrElseAsync[A <: Iterable[T]](block: => Future[A]): Future[ModelMap] = {
    getAll match {
      case Some(map) => Future.successful(map)
      case None => block map {
        models => models.map(model => (model._id, model)).toMap
      } andThen {
        case Success(result) => setAll(result)
      }
    }
  }

  /**
    * Get entity from the cache. If cache is empty, the given entity collection provider is used to
    * fetch the data, and the data is then put inside the cache if successful.
    *
    * @param id    query entity id
    * @param block asynchronous block to fetch all entities
    * @tparam A type of returned entity collection
    * @return [[util.FutureOption]] of the model
    */
  def getOrElseAsync[A <: Iterable[T]](id: String, block: => Future[A]): FutureOption[T] =
    FutureOption(getAllOrElseAsync(block) map (_ get id))

}