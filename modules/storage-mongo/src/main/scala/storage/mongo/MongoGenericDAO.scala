package storage.mongo

import cache.BaseModelMapCache
import models.BaseModel
import play.api.cache.CacheApi
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor._
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import storage.{GenericDAO, StorageException}
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, _}
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

abstract class MongoGenericDAO[T <: BaseModel[T]](cacheApi: CacheApi, collectionName: String) extends GenericDAO[T] {

  self: ReactiveMongoComponents with BSONContext[T] =>

  protected def cacheDuration: Duration = 1.days

  protected def cachePrefix = collectionName

  protected val cache = new BaseModelMapCache[T](cacheApi, cachePrefix, cacheDuration)

  implicit def collectionFuture: Future[BSONCollection] = reactiveMongoApi.database map (db => db.collection[BSONCollection](collectionName))

  protected def withCollection[A](block: BSONCollection => Future[A]): Future[A] = {
    collectionFuture flatMap (collection => block(collection) )
  }

  private[this] def loadFromDb: Future[Seq[T]] = withCollection { collection =>
    collection.find(BSONDocument()).cursor[T](Primary).collect(-1, FailOnError[Seq[T]]()) recover storageExceptionHandler("loading")
  }

  def getMap = cache getAllOrElseAsync loadFromDb

  def invalidateCache() = cache.removeAll()

  private[this] def createUniqueId: String = BSONObjectID.generate().stringify

  def insert(entity: T): Future[T] = insertWithGivenId(entity withId createUniqueId)

  def insertWithGivenId(entity: T): Future[T] = withCollection { collection =>
    collection.insert(entity) recover storageExceptionHandler("inserting into") map {
      case wr: WriteResult if wr.ok => entity
      case WriteResult.Message(errmsg) => throw new StorageException(s"Error inserting to collection $collectionName: $errmsg")
    } andThen {
      case Success(x) => cache.set(entity)
      case Failure(_) => cache.removeAll() // Reload to be on the safe side
    }
  }

  def update(id: String, modifier: BSONDocument): FutureOption[T] = {
    val selector = BSONDocument("_id" -> id)
    FutureOption(withCollection { collection => collection.update(selector, modifier)} flatMap {
      writeResult: WriteResult =>
        cache.removeAll()
        getById(id).toFuture
    } recover storageExceptionHandler("updating"))
  }

  def remove(id: String): Future[Boolean] = withCollection { collection =>
    val selector = BSONDocument("_id" -> id)
    collection.remove(selector, firstMatchOnly = true) map {
      case wr: WriteResult if wr.ok =>
        cache.remove(id)
        true
      case _ =>
        false
    } recover storageExceptionHandler("deleting")
  }

  private[this] def storageExceptionHandler(action: String): PartialFunction[Throwable, Nothing] = {
    case NonFatal(exc) =>
      exc.printStackTrace() // TODO: Proper logging
      throw new StorageException(s"Error $action collection $collectionName", exc)
  }

}
