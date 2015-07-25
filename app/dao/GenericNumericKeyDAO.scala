package dao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import exceptions.QueryException
import models.{ BaseModelIdReader, BaseModelIdWriter }
import reactivemongo.api.CursorProducer
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.commands.DefaultWriteResult
import reactivemongo.bson.{ BSONDocument, BSONDocumentReader, BSONDocumentWriter }
import reactivemongo.bson.Producer.nameValue2Producer
import models.BaseModel

trait GenericNumericKeyDAO[T] extends GenericDAO[T, Int] {

  def insertOptimistic(entity: T)(implicit baseModel: BaseModel[T], idReader: BaseModelIdReader[T, Int], idWriter: BaseModelIdWriter[T, Int],
                                  reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T],
                                  cp: CursorProducer[T]): Future[T] =
    getNextId flatMap {
      nextId =>
        val entityToInsert = idWriter.withId(entity, nextId)
        collection.insert(entityToInsert) flatMap {
          _ => Future.successful(entityToInsert)
        } andThen {
          case _ => cache.remove
        } recoverWith {
          // Duplicate key - try again
          case DefaultWriteResult(false, _, _, _, Some(11000), _) => insertOptimistic(entity)
        } recover {
          case exc => throw new QueryException(s"Error inserting to collection $getCollectionName", exc)
        }
    }

  def getNextId(implicit baseModel: BaseModel[T], idReader: BaseModelIdReader[T, Int], reader: BSONDocumentReader[T], cp: CursorProducer[T]): Future[Int] =
    collection.find(BSONDocument()).sort(BSONDocument("_id" -> -1)).cursor[T](Primary).headOption.map {
      case None         => 1
      case Some(entity) => idReader.getId(entity) + 1
    }

  def <<(entity: T)(implicit baseModel: BaseModel[T], idReader: BaseModelIdReader[T, Int], idWriter: BaseModelIdWriter[T, Int],
                    reader: BSONDocumentReader[T], writer: BSONDocumentWriter[T],
                    cp: CursorProducer[T]): Future[T] = insertOptimistic(entity)

}