package storage.mongo

import javax.inject.{Inject, Singleton}

import models.Thread
import org.joda.time.DateTime
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import storage.ThreadDAO

import scala.concurrent.Future

@Singleton
class MongoThreadDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Thread](cacheApi, "threads") with ReactiveMongoComponents with BSONContext[Thread] with ThreadDAO {

  override def bsonWriter = implicitly

  override def bsonReader = implicitly

  // TODO
  override def updateLastPost(id: String, user: String, date: DateTime): Future[Option[Thread]] = ???
}
