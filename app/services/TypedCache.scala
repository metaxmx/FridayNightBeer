package services

import play.api.Logger
import play.cache.Cache
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success

class TypedCache[T](entityQuery: T => String, queryKey: String => String, expiration: Int) {

  def get(query: String): Option[T] =
    Option(Cache.get(queryKey(query))).map(_.asInstanceOf[T])

  def set(entity: T): Unit = {
    Logger.info(s"Put key ${queryKey(entityQuery(entity))} into cache")
    Cache.set(queryKey(entityQuery(entity)), Some(entity), expiration)
  }

  def set(entityOpt: Option[T]): Unit = entityOpt match {
    case Some(entity) => set(entity)
    case None         => throw new IllegalArgumentException("cannot use set(None). Use remove(query) instead.")
  }

  def remove(query: String): Unit = {
    Logger.info(s"Remove key ${queryKey(query)} from cache")
		  Cache.remove(queryKey(query))
  }

  def getOrElseAsync(query: String, block: => Future[Option[T]]): Future[Option[T]] = {
    val key = queryKey(query)
    val foundValue = get(query)
    foundValue match {
      case Some(v) => {
        Logger.info(s"Found key $key in cache")
        Future.successful(Some(v))
      }
      case None => {
        Logger.info(s"Missing key $key in cache")
        block andThen {
          case Success(Some(result)) => set(result)
        }
      }
    }
  }

}