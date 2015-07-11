package util

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

import play.cache.Cache

/**
 * Utility class to access a typed shard of the application cache.
 * The TypedCache instance is bound to a type and makes sure, only instances of this type
 * are entered into the cache when accessing it through this class.
 * All getters and setter accept Option[T], but on the instances of T are put into the cache.
 */
class TypedCache[T](entityQuery: T => String, queryKey: String => String, expiration: Int) {

  def get(query: String): Option[T] = Option(Cache.get(queryKey(query))).map(_.asInstanceOf[T])

  def set(entity: T): Unit = Cache.set(queryKey(entityQuery(entity)), entity, expiration)

  def set(entityOpt: Option[T]): Unit = entityOpt match {
    case Some(entity) => set(entity)
    case None         => throw new IllegalArgumentException("cannot use set(None). Use remove(query) instead.")
  }

  def remove(query: String): Unit = Cache.remove(queryKey(query))

  def getOrElseAsync(query: String, block: => Future[Option[T]]): Future[Option[T]] = {
    val key = queryKey(query)
    val foundValue = get(query)
    foundValue match {
      case Some(v) => Future.successful(Some(v))
      case None => block andThen {
        case Success(Some(result)) => set(result)
      }
    }
  }

  def getOrElseAsyncDef(query: String, block: => Future[T]): Future[T] =
    getOrElseAsync(query, block map { v => Some(v) }) map { opt => opt.get }
}