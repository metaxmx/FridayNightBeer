package storage.mongo

import javax.inject.{Inject, Singleton}

import models.Group
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.GroupDAO

@Singleton
class MongoGroupDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Group](cacheApi, "groups") with ReactiveMongoComponents with BSONContext[Group] with GroupDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Group]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Group]]

}
