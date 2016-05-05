package storage.mongo

import javax.inject.{Inject, Singleton}

import models.Permission
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.PermissionDAO

@Singleton
class MongoPermissionDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Permission](cacheApi, "permissions") with ReactiveMongoComponents with BSONContext[Permission] with PermissionDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Permission]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Permission]]

}