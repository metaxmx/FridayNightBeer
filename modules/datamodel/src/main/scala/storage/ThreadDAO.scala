package storage

import models.Thread
import org.joda.time.DateTime

import scala.concurrent.Future

trait ThreadDAO extends GenericDAO[Thread] {

  def updateLastPost(id: String, user: String, date: DateTime): Future[Option[Thread]]

}