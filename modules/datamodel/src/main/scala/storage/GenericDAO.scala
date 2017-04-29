package storage

import models.BaseModel
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Generic and storage-independent Trait for data access objects.
  * @tparam T base model type
  */
trait GenericDAO[T <: BaseModel[T]] {

  /**
    * Get all entities as a map from id -> model
    * @return future of entities map
    */
  def getMap: Future[Map[String, T]]

  /**
    * Get all entities
    * @return future of all entities
    */
  def getAll: Future[Seq[T]] = getMap map (_.values.toSeq)

  /**
    * Select a single entities by its id.
    * @param id id to select
    * @return future-option of found entities
    */
  def getById(id: String): FutureOption[T] = FutureOption(getMap map (_ get id))

  /**
    * Get all entites and map result.
    * @param f mapping function
    * @tparam S map result type
    * @return future of mapped result
    */
  def map[S](f: Seq[T] => S): Future[S] = getAll map f

  /**
    * Assign the entity a new unique if and insert it into the database.
    * @param entity entity data
    * @return future of the inserted entity (including the newly assigned id)
    */
  def insert(entity: T): Future[T]

  /**
    * Insert the entity (with the id it already contains)
    * @param entity entity data
    * @return uture of the inserted entity
    */
  def insertWithGivenId(entity: T): Future[T]

  /**
    * Remove single entity.
    * @param id id to select for
    * @return future of deletion success (true for a deleted entity)
    */
  def remove(id: String): Future[Boolean]

  /**
    * Remove single entity.
    * @param entity entity to select for
    * @return future of deletion success (true for a deleted entity)
    */
  def remove(entity: T): Future[Boolean] = remove(entity._id)

  /**
    * Invalidate cached entities.
    */
  def invalidateCache(): Unit

}

