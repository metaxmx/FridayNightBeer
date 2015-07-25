package dao

import javax.inject.Singleton

import models.ForumCategory

@Singleton
class ForumCategoryDAO extends GenericNumericKeyDAO[ForumCategory] {

  override def getCacheKey = "db.categories"

}