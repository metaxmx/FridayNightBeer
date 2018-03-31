package storage.mongo

import javax.inject.{Inject, Singleton}
import models.User
import play.api.cache.SyncCacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.UserDAO

@Singleton
class MongoUserDAOInstance @Inject()(cacheApi: SyncCacheApi, reactiveMongoApi: ReactiveMongoApi)
  extends MongoUserDAO(cacheApi, reactiveMongoApi)

class MongoUserDAO(cacheApi: SyncCacheApi, val reactiveMongoApi: ReactiveMongoApi, dbCollectionSuffix: Option[String] = None)
  extends MongoGenericDAO[User](cacheApi, "users", dbCollectionSuffix)
    with ReactiveMongoComponents with BSONContext[User] with UserDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[User]]

  implicit val bsonReader = implicitly[BSONDocumentReader[User]]

}
