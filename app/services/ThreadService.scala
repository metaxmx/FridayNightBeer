package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import dao.ThreadDAO
import models.Thread

@Singleton
class ThreadService @Inject() (threadDAO: ThreadDAO) {

  def getThread(id: Int): Future[Option[Thread]] = threadDAO ?? id

  def getThreadsByForum: Future[Map[Int, Seq[Thread]]] = threadDAO >> { _.groupBy { _.forum } }

  def insertThread(thread: Thread): Future[Thread] = threadDAO << thread

}