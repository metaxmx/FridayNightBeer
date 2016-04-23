package dao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.modules.reactivemongo.ReactiveMongoComponents
import exceptions.QueryException
import reactivemongo.api.ReadPreference.Primary
import reactivemongo.api.commands.DefaultWriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.Producer.nameValue2Producer
import reactivemongo.core.errors.DatabaseException

trait GenericNumericKeyDAO[T] extends GenericDAO[T, Int] {

  self: ReactiveMongoComponents with BaseModelComponents[T, Int] =>

  def insertOptimistic(entity: T): Future[T] = getNextId flatMap {
    nextId =>
      val entityToInsert = baseModelIdWriter.withId(entity, nextId)
      collection.insert(entityToInsert) flatMap {
        _ => Future.successful(entityToInsert)
      } andThen {
        case _ => cache.remove
      } recoverWith {
        // Duplicate key - try again
        case e: DatabaseException if e.code contains 11000 => insertOptimistic(entity)
      } recover {
        case exc => throw new QueryException(s"Error inserting to collection $getCollectionName", exc)
      }
  }

  def getNextId: Future[Int] = collection.find(BSONDocument()).sort(BSONDocument("_id" -> -1)).cursor[T](Primary).headOption.map {
    case None         => 1
    case Some(entity) => (baseModelIdReader getId entity) + 1
  }

  def <<(entity: T): Future[T] = insertOptimistic(entity)

}
