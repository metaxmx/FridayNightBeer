package util

import scala.concurrent.Future

/**
 * Extension of TypedCache with a fixed key for singleton without the need to differenciate
 * between multiple instances in the application cache.
 */
class TypedSingletonCache[T](key: String, expiration: Int) extends TypedCache[T](e => key, s => key, expiration) {

  def get: Option[T] = get(key)

  def remove: Unit = remove(key)

  def getOrElseAsync(block: => Future[Option[T]]): Future[Option[T]] = getOrElseAsync(key, block)

  def getOrElseAsyncDef(block: => Future[T]): Future[T] = getOrElseAsyncDef(key, block)

}