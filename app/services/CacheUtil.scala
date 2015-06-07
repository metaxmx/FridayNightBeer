package services

import play.cache.Cache
import scala.concurrent.Future
import java.util.concurrent.Callable
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

object CacheUtil {

  def getOrElseAsync[T](key: String, block: => Future[T], expiration: Int): Future[T] =
    Option(Cache.get(key)) match {
      case Some(v) => {
        Logger.info(s"Found key $key in cache")
        Future.successful(v.asInstanceOf[T])
      }
      case None => {
        Logger.info(s"Missing key $key in cache")
        block flatMap {
          result =>
            Cache.set(key, result, expiration)
            Future.successful(result)
        }
      }
    }

}