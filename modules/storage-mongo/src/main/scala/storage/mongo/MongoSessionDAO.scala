package storage.mongo

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import storage.SessionDAO

import scala.concurrent.Future

@Singleton
class MongoSessionDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[UserSession](cacheApi, "sessions") with ReactiveMongoComponents with BSONContext[UserSession] with SessionDAO {

  override def bsonWriter = implicitly

  override def bsonReader = implicitly

  // TODO
  override def updateSessionUser(id: String, userOpt: Option[User]): Future[Option[UserSession]] = ???
}
