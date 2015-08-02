package dao

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.{ BaseModelSpec, User, UserSession }
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.Producer.nameValue2Producer

@Singleton
class SessionDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericDAO[UserSession, String] with ReactiveMongoComponents with BaseModelComponents[UserSession, String] {

  override def spec: BaseModelSpec[UserSession, String] = implicitly

  override def getCacheKey = "db.sessions"

  def updateSessionUser(id: String, userOpt: Option[User]): Future[Option[UserSession]] = {
    val selector = BSONDocument("_id" -> id)
    val modifier = userOpt.fold {
      BSONDocument(
        "$unset" -> BSONDocument(
          "user_id" -> 1))
    } {
      user =>
        BSONDocument(
          "$set" -> BSONDocument(
            "user_id" -> user._id))
    }
    update(id, selector, modifier)
  }

}