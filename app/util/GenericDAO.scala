package util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.bson.BSONDocument
import models.BaseModelIdWriter
import models.BaseModelIdReader
import reactivemongo.bson.BSONDocumentReader
import reactivemongo.api.CursorProducer
import play.api.Logger
import reactivemongo.bson.BSONDocumentWriter
import reactivemongo.api.commands.DefaultWriteResult
import exceptions.QueryException
import reactivemongo.bson.Producer.nameValue2Producer

trait GenericDAO[T] {

  def findAll(implicit collection: BSONCollection, reader: BSONDocumentReader[T], cp: CursorProducer[T], names: EntityName): Future[Seq[T]] = {
    collection.find(BSONDocument()).cursor[T](Primary).collect[Seq]() recover {
      case exc => {
        Logger.error(s"Error loading ${names.names}", exc)
        throw new QueryException(s"Error loading ${names.names}", exc)
      }
    }
  }

  def insertOptimistic(entity: T)(implicit collection: BSONCollection, idReader: BaseModelIdReader[T],
                                  idWriter: BaseModelIdWriter[T], reader: BSONDocumentReader[T],
                                  writer: BSONDocumentWriter[T], cp: CursorProducer[T], names: EntityName): Future[T] =
    getNextId flatMap {
      nextId =>
        {
          Logger info s"Found next ID $nextId"
          val entityToInsert = idWriter.withId(entity, nextId)
          collection.insert(entityToInsert) flatMap {
            _ => Future.successful(entityToInsert)
          } recoverWith {
            // Duplicate key - try again
            case DefaultWriteResult(false, _, _, _, Some(11000), _) => insertOptimistic(entity)
          } recover {
            case exc =>
              Logger.error(s"Error inserting ${names.names}", exc)
              throw new QueryException(s"Error inserting ${names.names}", exc)
          }
        }
    }

  def getNextId(implicit collection: BSONCollection, idReader: BaseModelIdReader[T],
                reader: BSONDocumentReader[T], cp: CursorProducer[T]): Future[Int] =
    collection.find(BSONDocument()).sort(BSONDocument("_id" -> -1)).cursor[T](Primary).headOption.map {
      case None         => 1
      case Some(entity) => idReader.getId(entity) + 1
    }

}

case class EntityName(name: String, names: String)

object EntityName {

  def apply(name: String): EntityName = EntityName(name, s"${name}s")

}