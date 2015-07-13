package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import dao.ForumDAO
import exceptions.ApiException.{ accessDeniedException, dbException, notFoundException }
import exceptions.QueryException
import models.{ Forum, User }

@Singleton
class ForumService @Inject() (forumDAO: ForumDAO) {

  def getForum(id: Int): Future[Option[Forum]] = forumDAO ?? id

  def getForumsByCategory: Future[Map[Int, Seq[Forum]]] = forumDAO >> { _.groupBy { _.category } }

  def getForumForApi(id: Int)(implicit userOpt: Option[User]): Future[Forum] = getForum(id) map {
    case None        => notFoundException
    case Some(forum) => if (forum.accessGranted) forum else accessDeniedException
  } recover {
    case e: QueryException => dbException
  }

  def getForumsByCategoryForApi = getForumsByCategory recover {
    case e: QueryException => dbException
  }

}

