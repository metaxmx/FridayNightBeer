package storage.mongo

import javax.inject.{Inject, Singleton}
import models.Forum
import play.api.cache.SyncCacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.ForumDAO

@Singleton
class MongoForumDAO @Inject()(cacheApi: SyncCacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Forum](cacheApi, "forums") with ReactiveMongoComponents with BSONContext[Forum] with ForumDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Forum]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Forum]]

}
