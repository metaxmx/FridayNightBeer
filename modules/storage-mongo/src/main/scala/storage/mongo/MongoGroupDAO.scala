package storage.mongo

import javax.inject.{Inject, Singleton}

import models.{Forum, Group}
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import storage.GroupDAO

@Singleton
class MongoGroupDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Group](cacheApi, "groups") with ReactiveMongoComponents with BSONContext[Group] with GroupDAO {

  override def bsonWriter = implicitly

  override def bsonReader = implicitly
}
