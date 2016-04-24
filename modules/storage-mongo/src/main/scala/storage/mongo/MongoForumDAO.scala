package storage.mongo

import javax.inject.{Inject, Singleton}

import models.Forum
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import storage.ForumDAO

@Singleton
class MongoForumDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Forum](cacheApi, "forums") with ReactiveMongoComponents with BSONContext[Forum] with ForumDAO {

  override def bsonWriter = implicitly

  override def bsonReader = implicitly
}
