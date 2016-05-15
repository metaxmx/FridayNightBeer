package storage.mongo

import javax.inject.{Inject, Singleton}

import models.{Thread, ThreadPostData}
import org.joda.time.DateTime
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONBoolean, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONInteger}
import storage.ThreadDAO
import util.FutureOption

import scala.concurrent.Future

@Singleton
class MongoThreadDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[Thread](cacheApi, "threads") with ReactiveMongoComponents with BSONContext[Thread] with ThreadDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[Thread]]

  implicit val bsonReader = implicitly[BSONDocumentReader[Thread]]

  override def updateLastPost(id: String, user: String, date: DateTime): FutureOption[Thread] = {
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "lastPost" -> ThreadPostData(user, date)))
    update(id, modifier)
  }

  override def updatePostCount(id: String, posts: Integer): FutureOption[Thread] = {
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "posts" -> BSONInteger(posts)))
    update(id, modifier)
  }

  override def updateSticky(id: String, sticky: Boolean): FutureOption[Thread] = {
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "sticky" -> BSONBoolean(sticky)))
    update(id, modifier)
  }

  override def updateClosed(id: String, closed: Boolean): FutureOption[Thread] = {
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "closed" -> BSONBoolean(closed)))
    update(id, modifier)
  }

}
