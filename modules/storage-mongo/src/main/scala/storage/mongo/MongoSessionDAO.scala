package storage.mongo

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import storage.SessionDAO
import util.FutureOption

import scala.concurrent.Future

@Singleton
class MongoSessionDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[UserSession](cacheApi, "sessions") with ReactiveMongoComponents with BSONContext[UserSession] with SessionDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[UserSession]]

  implicit val bsonReader = implicitly[BSONDocumentReader[UserSession]]

  override def updateSessionUser(id: String, userOpt: Option[User]): FutureOption[UserSession] = {
    val modifier = userOpt.fold {
      BSONDocument(
        "$unset" -> BSONDocument(
          "user" -> 1))
    } {
      user =>
        BSONDocument(
          "$set" -> BSONDocument(
            "user" -> user._id))
    }
    update(id, modifier)
  }

}
