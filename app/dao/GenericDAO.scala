package dao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.modules.reactivemongo.ReactiveMongoComponents

import exceptions.QueryException
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import util.{ IndexedEntityCollection, TypedSingletonCache }

trait GenericDAO[T, K] {

  self: ReactiveMongoComponents with BaseModelComponents[T, K] =>

  def getCacheInterval: Int = 60 * 60 * 24

  def getCacheKey: String

  def getCollectionName: String = baseModel.collectionName

  val cache = new TypedSingletonCache[IndexedEntityCollection[T, K]](getCacheKey, getCacheInterval)

  implicit def collection = reactiveMongoApi.db.collection[BSONCollection](getCollectionName)

  def getAll: Future[Seq[T]] = getIndexedCollection map { _.entities }

  def getById(id: K): Future[Option[T]] = getIndexedCollection map { _.entitiesById get id }

  def getIndexedCollection: Future[IndexedEntityCollection[T, K]] =
    cache.getOrElseAsyncDef { findAll map { IndexedEntityCollection[T, K](_) } }

  def findAll: Future[Seq[T]] = collection.find(BSONDocument()).cursor[T](Primary).collect[Seq]() recover {
    case exc => throw new QueryException(s"Error loading from collection $getCollectionName", exc)
  }

  def ??(id: K): Future[Option[T]] = getById(id)

  def map[S](f: Seq[T] => S): Future[S] = getAll map f

  def >>[S](f: Seq[T] => S): Future[S] = map(f)

  def getIndex: Future[Map[K, T]] = getIndexedCollection map { _.entitiesById }

  def insertWithGivenId(entity: T): Future[T] =
    collection.insert(entity) flatMap {
      _ => Future.successful(entity)
    } andThen {
      case _ => cache.remove
    } recover {
      case exc => throw new QueryException(s"Error inserting to collection $getCollectionName", exc)
    }

  def <<!(entity: T): Future[T] = insertWithGivenId(entity)

  def update(id: K, selector: BSONDocument, modifier: BSONDocument): Future[Option[T]] = {
    collection.update(selector, modifier) flatMap {
      _ =>
        cache.remove
        getById(id)
    } recover {
      case exc => throw new QueryException(s"Error updating collection $getCollectionName", exc)
    }
  }

  def invalidateCache = cache.remove

}

