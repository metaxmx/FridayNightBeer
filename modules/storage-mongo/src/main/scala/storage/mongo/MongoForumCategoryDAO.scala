package storage.mongo

import javax.inject.{Inject, Singleton}

import models.ForumCategory
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import storage.ForumCategoryDAO

@Singleton
class MongoForumCategoryDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[ForumCategory](cacheApi, "categories") with ReactiveMongoComponents with BSONContext[ForumCategory] with ForumCategoryDAO {

  override def bsonWriter = implicitly

  override def bsonReader = implicitly
}
