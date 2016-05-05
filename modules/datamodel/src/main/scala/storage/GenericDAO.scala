package storage

import models.BaseModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Generic and storage-independent Trait for data access objects.
  * @tparam T base model type
  */
trait GenericDAO[T <: BaseModel[T]] {

  def getMap: Future[Map[String, T]]

  def getAll: Future[Seq[T]] = getMap map (_.values.toSeq)

  def getById(id: String): Future[Option[T]] = getMap map (_ get id)

  def ??(id: String): Future[Option[T]] = getById(id)

  def map[S](f: Seq[T] => S): Future[S] = getAll map f

  def >>[S](f: Seq[T] => S): Future[S] = map(f)

  def insert(entity: T): Future[T]

  def <<(entity: T): Future[T] = insert(entity)

  def insertWithGivenId(entity: T): Future[T]

  def <<!(entity: T): Future[T] = insertWithGivenId(entity)

  def remove(id: String): Future[Boolean]

  def ><(id: String): Future[Boolean] = remove(id)

  def remove(entity: T): Future[Boolean] = remove(entity._id)

  def ><(entity: T): Future[Boolean] = remove(entity._id)

  def invalidateCache(): Unit

}

