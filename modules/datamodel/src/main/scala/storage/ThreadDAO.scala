package storage

import models.Thread
import org.joda.time.DateTime
import util.FutureOption

import scala.concurrent.Future

trait ThreadDAO extends GenericDAO[Thread] {

  def updateLastPost(id: String, user: String, date: DateTime): FutureOption[Thread]

}