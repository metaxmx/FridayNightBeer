package com.fridaynightbeer.db

import com.fridaynightbeer.entity.KeyedEntity

import scala.concurrent.Future

/**
  * Generic DAO abstraction
  *
  * @tparam T entity type
  */
trait GenericDao[T <: KeyedEntity] {

  /**
    * Find all entities
    *
    * @return all entities
    */
  def findAll(): Future[Seq[T]]

  /**
    * Find single entity by id
    *
    * @param id single entity
    * @return optional found entity
    */
  def findById(id: String): Future[Option[T]]

  /**
    * Insert entity into database
    *
    * @param entity new entity
    */
  def insert(entity: T): Future[Unit]

  /**
    * Insert multiple entities into database
    * @param entities new entities
    */
  def insertAll(entities: Seq[T]): Future[Unit]

  /**
    * Update entity.
    *
    * @param entity updated entities
    */
  def update(entity: T): Future[Unit]

  /**
    * Delete entity by id.
    *
    * @param id id to delete
    * @return true if an entity was deleted
    */
  def delete(id: String): Future[Boolean]

  /**
    * Delete an entity
    *
    * @param entity entity to delete
    * @return true if an entity was deleted
    */
  def delete(entity: T): Future[Boolean] = delete(entity.id)

}
