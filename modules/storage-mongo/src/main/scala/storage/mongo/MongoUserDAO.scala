package storage.mongo

import javax.inject.{Inject, Singleton}

import models.User
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.UserDAO

@Singleton
class MongoUserDAOInstance @Inject()(cacheApi: CacheApi, reactiveMongoApi: ReactiveMongoApi)
  extends MongoUserDAO(cacheApi, reactiveMongoApi)

class MongoUserDAO(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi, dbCollectionSuffix: Option[String] = None)
  extends MongoGenericDAO[User](cacheApi, "users", dbCollectionSuffix)
    with ReactiveMongoComponents with BSONContext[User] with UserDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[User]]

  implicit val bsonReader = implicitly[BSONDocumentReader[User]]

}
