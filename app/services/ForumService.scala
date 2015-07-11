package services

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

import dao.ForumDAO
import models.Forum

@Singleton
class ForumService @Inject() (forumDAO: ForumDAO) {

  def getForum(id: Int): Future[Option[Forum]] = forumDAO ?? id

  def getForumsByCategory: Future[Map[Int, Seq[Forum]]] = forumDAO >> { _.groupBy { _.category } }

}

