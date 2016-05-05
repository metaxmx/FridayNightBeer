package services

import javax.inject.{Inject, Singleton}

import models.ForumCategory
import storage.ForumCategoryDAO
import util.FutureOption

import scala.concurrent.Future

@Singleton
class ForumCategoryService @Inject()(forumCategoryDAO: ForumCategoryDAO) {

  def getCategory(id: String): FutureOption[ForumCategory] = forumCategoryDAO ?? id

  def getCategories: Future[Seq[ForumCategory]] = forumCategoryDAO.getAll

  def insertCategory(category: ForumCategory): Future[ForumCategory] = forumCategoryDAO << category

}

