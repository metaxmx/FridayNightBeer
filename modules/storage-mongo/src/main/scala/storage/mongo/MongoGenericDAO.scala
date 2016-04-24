package storage.mongo

import cache.BaseModelMapCache
import models.BaseModel
import play.api.cache.CacheApi
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.CursorProducer
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import storage.{GenericDAO, StorageException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, _}
import scala.util.{Failure, Success}

abstract class MongoGenericDAO[T <: BaseModel[T]](cacheApi: CacheApi, collectionName: String) extends GenericDAO[T] {

  self: ReactiveMongoComponents with BSONContext[T] =>

  protected def cacheDuration: Duration = 1 days

  protected def cachePrefix = collectionName

  protected lazy implicit val implicitBsonReader = bsonReader

  protected lazy implicit val implicitBsonWriter = bsonWriter

  protected val cache = new BaseModelMapCache[T](cacheApi, cachePrefix, cacheDuration)

  implicit def collection = reactiveMongoApi.db.collection[BSONCollection](collectionName)

  private[this] def loadFromDb: Future[Seq[T]] =
    collection.find(BSONDocument()).cursor[T](Primary).collect[Seq]() recover {
      case exc: Exception => throw new StorageException(s"Error loading from collection $collectionName", exc)
    }

  def getMap = cache getAllOrElseAsync loadFromDb

  def invalidateCache() = cache.removeAll()

  private[this] def createUniqueId: String = BSONObjectID.generate().stringify

  def insert(entity: T): Future[T] = insertWithGivenId(entity withId createUniqueId)

  def insertWithGivenId(entity: T): Future[T] =
    collection.insert(entity) recover {
      case exc => throw new StorageException(s"Error inserting to collection $collectionName", exc)
    } map {
      case wr: WriteResult if wr.ok => entity
      case wr: WriteResult => throw new StorageException(s"Error inserting to collection $collectionName: ${wr.message}")
    } andThen {
      case Success(x) => cache.set(entity)
      case Failure(_) => cache.removeAll() // Reload to be on the safe side
    }

  //  def update(id: String, selector: BSONDocument, modifier: BSONDocument): Future[Option[T]] = {
  //    collection.update(selector, modifier) flatMap {
  //      writeResult: WriteResult =>
  //        cache.remove(id)
  //        getById(id)
  //    } recover {
  //      case exc => throw new StorageException(s"Error updating collection $collectionName", exc)
  //    }
  //  }

}
