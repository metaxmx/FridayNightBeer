package storage

import models.Thread
import org.joda.time.DateTime
import util.FutureOption

trait ThreadDAO extends GenericDAO[Thread] {

  def updateLastPost(id: String, user: String, date: DateTime): FutureOption[Thread]

  def updatePostCount(id: String, posts: Integer): FutureOption[Thread]

  def updateSticky(id: String, sticky: Boolean): FutureOption[Thread]

  def updateClosed(id: String, closed: Boolean): FutureOption[Thread]

}
