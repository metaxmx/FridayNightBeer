package services

import javax.inject.{Inject, Singleton}

import models.Forum
import storage.ForumDAO
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ForumService @Inject()(forumDAO: ForumDAO) {

  def getForum(id: String): FutureOption[Forum] = forumDAO ?? id

  def getForumOrElse(id: String, onEmpty: => Forum) = getForum(id) flatten onEmpty

  def getForumByUrl(url: String): FutureOption[Forum] = getForum(url) orElse FutureOption(forumDAO.getAll.map(_.find(_.url.contains(url))))

  def getForumByUrlOrElse(url: String, onEmpty: => Forum) = getForumByUrl(url) flatten onEmpty

  def getForumsByCategory: Future[Map[String, Seq[Forum]]] = forumDAO >> (_.groupBy(_.category))

  def insertForum(forum: Forum): Future[Forum] = forumDAO << forum

}

