package dao

import javax.inject.Singleton

import scala.concurrent.Future

import org.joda.time.DateTime

import models.{ Thread, ThreadPostData }
import reactivemongo.bson.BSONDocument
import reactivemongo.bson.Producer.nameValue2Producer

@Singleton
class ThreadDAO extends GenericNumericKeyDAO[Thread] {

  override def getCacheKey = "db.threads"

  def updateLastPost(id: Int, user: Int, date: DateTime): Future[Option[Thread]] = {
    val selector = BSONDocument("_id" -> id)
    val modifier = BSONDocument(
      "$set" -> BSONDocument(
        "lastPost" -> ThreadPostData(user, date)))
    update(id, selector, modifier)
  }

}