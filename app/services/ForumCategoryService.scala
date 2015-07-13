package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import dao.ForumCategoryDAO
import models.ForumCategory

@Singleton
class ForumCategoryService @Inject() (forumCategoryDAO: ForumCategoryDAO) {

  def getCategory(id: Int): Future[Option[ForumCategory]] = forumCategoryDAO ?? id

  def getCategories: Future[Seq[ForumCategory]] = forumCategoryDAO.getAll

}

