package storage

import models.Thread
import org.joda.time.DateTime
import util.FutureOption

/**
  * DAO for threads.
  */
trait ThreadDAO extends GenericDAO[Thread] {

  /**
    * Update the latest post of a thread.
    * @param id thread id
    * @param user new latest user id
    * @param date latest post date
    * @return future-option of the changed thread
    */
  def updateLastPost(id: String, user: String, date: DateTime): FutureOption[Thread]

  /**
    * Update the post-count of a thread.
    * @param id thread
    * @param posts new number of posts
    * @return future-option of the changed thread
    */
  def updatePostCount(id: String, posts: Integer): FutureOption[Thread]

  /**
    * Update the sticky-flag of a thread.
    * @param id thread
    * @param sticky new sticky flag
    * @return future-option of the changed thread
    */
  def updateSticky(id: String, sticky: Boolean): FutureOption[Thread]

  /**
    * Update the closed-flag of a thread.
    * @param id thread
    * @param closed new closed flag
    * @return future-option of the changed thread
    */
  def updateClosed(id: String, closed: Boolean): FutureOption[Thread]

}
