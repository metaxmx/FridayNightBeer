package services

import javax.inject.{Inject, Singleton}

import exceptions.ApiExceptions.{accessDeniedException, dbException, notFoundException}
import exceptions.QueryException
import models.{Thread, User}
import org.joda.time.DateTime
import storage.ThreadDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ThreadService @Inject()(threadDAO: ThreadDAO) {

  def getThread(id: String): Future[Option[Thread]] = threadDAO ?? id

  def getThreadsByForum: Future[Map[String, Seq[Thread]]] = threadDAO >> (_.groupBy(_.forum))

  def insertThread(thread: Thread): Future[Thread] = threadDAO << thread

  def getThreadForApi(id: String)(implicit userOpt: Option[User]): Future[Thread] = getThread(id) map {
    case None => notFoundException
    case Some(thread) => if (thread.accessGranted) thread else accessDeniedException
  } recover {
    case e: QueryException => dbException(e)
  }

  def getThreadsByForumForApi = getThreadsByForum recover {
    case e: QueryException => dbException(e)
  }

  def updateLastPostForApi(id: String, user: String, date: DateTime) = threadDAO.updateLastPost(id, user, date) recover {
    case e: QueryException => dbException(e)
  }

}