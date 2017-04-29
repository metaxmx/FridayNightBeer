package services

import javax.inject.{Inject, Singleton}

import models.{Forum, ForumCategory, Thread}
import org.joda.time.DateTime
import storage.{ForumCategoryDAO, ForumDAO, ThreadDAO}
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ThreadService @Inject()(threadDAO: ThreadDAO,
                              forumDAO: ForumDAO,
                              categoryDAO: ForumCategoryDAO) {

  def getThread(id: String): FutureOption[Thread] = threadDAO getById id

  def getThreadOrElse(id: String, onEmpty: => Thread): Future[Thread] = getThread(id) flatten onEmpty

  def getThreadsByForum: Future[Map[String, Seq[Thread]]] = threadDAO map (_.groupBy(_.forum))

  def getThreadsForForum(forumId: String): Future[Seq[Thread]] = getThreadsByForum map (_.getOrElse(forumId, Seq.empty))

  def insertThread(thread: Thread): Future[Thread] = threadDAO insert thread

  def updateLastPost(id: String, user: String, date: DateTime) = threadDAO.updateLastPost(id, user, date)

  def updateSticky(id: String, sticky: Boolean) = threadDAO.updateSticky(id, sticky)

  def updateClosed(id: String, closed: Boolean) = threadDAO.updateClosed(id, closed)

  def getThreadWithForum(id: String): FutureOption[(Thread, Forum, ForumCategory)] = for {
    thread <- getThread(id)
    forum <- forumDAO getById thread.forum
    cat <- categoryDAO getById forum.category
  } yield (thread, forum, cat)

}
