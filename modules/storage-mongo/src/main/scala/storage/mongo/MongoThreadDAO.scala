package storage.mongo

import javax.inject.{Inject, Singleton}

import models.{Thread, ThreadPostData}
import org.joda.time.DateTime
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import storage.ThreadDAO

import scala.concurrent.Future

@Singleton
class MongoThreadDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Thread](cacheApi, "threads") with ReactiveMongoComponents with BSONContext[Thread] with ThreadDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Thread]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Thread]]

  override def updateLastPost(id: String, user: String, date: DateTime): Future[Option[Thread]] = {
    val selector = BSONDocument("_id" -> id)
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "lastPost" -> ThreadPostData(user, date)))
    update(id, selector, modifier)
  }

}
