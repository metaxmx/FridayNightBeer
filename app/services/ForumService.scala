package services

import javax.inject.{Inject, Singleton}

import exceptions.ApiExceptions.{dbException, notFoundException}
import exceptions.QueryException
import models.Forum
import storage.ForumDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ForumService @Inject()(forumDAO: ForumDAO) {

  def getForum(id: String): Future[Option[Forum]] = forumDAO ?? id

  def getForumsByCategory: Future[Map[String, Seq[Forum]]] = forumDAO >> (_.groupBy(_.category))

  def getForumForApi(id: String): Future[Forum] = getForum(id) map {
    case None => notFoundException
    case Some(forum) => forum
  } recover {
    case e: QueryException => dbException(e)
  }

  def getForumsByCategoryForApi = getForumsByCategory recover {
    case e: QueryException => dbException(e)
  }

}

