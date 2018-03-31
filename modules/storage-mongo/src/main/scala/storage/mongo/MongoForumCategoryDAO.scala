package storage.mongo

import javax.inject.{Inject, Singleton}
import models.ForumCategory
import play.api.cache.SyncCacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.ForumCategoryDAO

@Singleton
class MongoForumCategoryDAO @Inject()(cacheApi: SyncCacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[ForumCategory](cacheApi, "categories") with ReactiveMongoComponents with BSONContext[ForumCategory] with ForumCategoryDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[ForumCategory]]

  implicit val bsonReader = implicitly[BSONDocumentReader[ForumCategory]]

}
