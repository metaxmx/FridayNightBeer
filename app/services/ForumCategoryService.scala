package services

import javax.inject.{Inject, Singleton}

import exceptions.ApiExceptions.{dbException, notFoundException}
import exceptions.QueryException
import models.ForumCategory
import storage.ForumCategoryDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ForumCategoryService @Inject()(forumCategoryDAO: ForumCategoryDAO) {

  def getCategory(id: String): Future[Option[ForumCategory]] = forumCategoryDAO ?? id

  def getCategories: Future[Seq[ForumCategory]] = forumCategoryDAO.getAll

  def insertCategory(category: ForumCategory): Future[ForumCategory] = forumCategoryDAO << category

  def getCategoryForApi(id: String): Future[ForumCategory] = getCategory(id) map {
    case None => notFoundException
    case Some(category) => category
  } recover {
    case e: QueryException => dbException(e)
  }

  def getCategoriesForApi = getCategories recover {
    case e: QueryException => dbException(e)
  }

}

