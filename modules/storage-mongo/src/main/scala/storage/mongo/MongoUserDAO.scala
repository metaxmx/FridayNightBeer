package storage.mongo

import javax.inject.{Inject, Singleton}

import models.User
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.UserDAO

@Singleton
class MongoUserDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[User](cacheApi, "users") with ReactiveMongoComponents with BSONContext[User] with UserDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[User]]

  implicit val bsonReader = implicitly[BSONDocumentReader[User]]

}
