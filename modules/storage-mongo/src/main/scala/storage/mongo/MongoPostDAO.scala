package storage.mongo

import javax.inject.{Inject, Singleton}

import models.Post
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.PostDAO

@Singleton
class MongoPostDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Post](cacheApi, "posts") with ReactiveMongoComponents with BSONContext[Post] with PostDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Post]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Post]]

}
