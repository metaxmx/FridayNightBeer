package dao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Play.current
import play.modules.reactivemongo.ReactiveMongoPlugin.db

import exceptions.QueryException
import models.BaseModelIdReader
import reactivemongo.api.CursorProducer
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter }
import util.{ IndexedEntityCollection, TypedSingletonCache }

trait GenericDAO[T, K] {

  def getCacheInterval: Int = 60 * 10

  def getCacheKey: String

  def getCollectionName: String

  val cache = new TypedSingletonCache[IndexedEntityCollection[T, K]](getCacheKey, getCacheInterval)

  implicit def collection = db.collection[BSONCollection](getCollectionName)

  def getAll(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
             idReader: BaseModelIdReader[T, K]): Future[Seq[T]] =
    getIndexedCollection map { _.entities }

  def getById(id: K)(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                     idReader: BaseModelIdReader[T, K]): Future[Option[T]] =
    getIndexedCollection map { _.entitiesById.get(id) }

  def getIndexedCollection(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                           idReader: BaseModelIdReader[T, K]): Future[IndexedEntityCollection[T, K]] =
    cache.getOrElseAsyncDef { findAll map { IndexedEntityCollection[T, K](_) } }

  def findAll(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T]): Future[Seq[T]] =
    collection.find(BSONDocument()).cursor[T](Primary).collect[Seq]() recover {
      case exc => throw new QueryException(s"Error loading from collection $getCollectionName", exc)
    }

  def ??(id: K)(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                idReader: BaseModelIdReader[T, K]): Future[Option[T]] = getById(id)

  def map[S](f: Seq[T] => S)(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                             idReader: BaseModelIdReader[T, K]): Future[S] = getAll map f

  def >>[S](f: Seq[T] => S)(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                            idReader: BaseModelIdReader[T, K]): Future[S] = map(f)

  def getIndex(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
               idReader: BaseModelIdReader[T, K]): Future[Map[K, T]] =
    getIndexedCollection map { _.entitiesById }

  def insertWithGivenId(entity: T)(implicit writer: BSONDocumentWriter[T]): Future[T] =
    collection.insert(entity) flatMap {
      _ => Future.successful(entity)
    } andThen {
      case _ => cache.remove
    } recover {
      case exc => throw new QueryException(s"Error inserting to collection $getCollectionName", exc)
    }

  def <<!(entity: T)(implicit writer: BSONDocumentWriter[T]): Future[T] = insertWithGivenId(entity)

  def update(id: K, selector: BSONDocument, modifier: BSONDocument)(implicit reader: BSONDocumentReader[T], cp: CursorProducer[T],
                                                                    idReader: BaseModelIdReader[T, K]): Future[Option[T]] = {
    collection.update(selector, modifier) flatMap {
      _ =>
        cache.remove
        getById(id)
    } recover {
      case exc => throw new QueryException(s"Error inserting to collection $getCollectionName", exc)
    }
  }

}

