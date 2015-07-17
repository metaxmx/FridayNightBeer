package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import dao.ThreadDAO
import exceptions.ApiException.{ accessDeniedException, dbException, notFoundException }
import exceptions.QueryException
import models.{ Thread, User }

@Singleton
class ThreadService @Inject() (threadDAO: ThreadDAO) {

  def getThread(id: Int): Future[Option[Thread]] = threadDAO ?? id

  def getThreadsByForum: Future[Map[Int, Seq[Thread]]] = threadDAO >> { _.groupBy { _.forum } }

  def insertThread(thread: Thread): Future[Thread] = threadDAO << thread

  def getThreadForApi(id: Int)(implicit userOpt: Option[User]): Future[Thread] = getThread(id) map {
    case None         => notFoundException
    case Some(thread) => if (thread.accessGranted) thread else accessDeniedException
  } recover {
    case e: QueryException => dbException
  }

  def getThreadsByForumForApi = getThreadsByForum recover {
    case e: QueryException => dbException
  }

}