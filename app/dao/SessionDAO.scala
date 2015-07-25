package dao

import javax.inject.Singleton

import scala.concurrent.Future

import models.{ User, UserSession }
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.Producer.nameValue2Producer

@Singleton
class SessionDAO extends GenericDAO[UserSession, String] {

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