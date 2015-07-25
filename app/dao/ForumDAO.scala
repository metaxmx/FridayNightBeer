package dao

import javax.inject.Singleton

import models.Forum

@Singleton
class ForumDAO extends GenericNumericKeyDAO[Forum] {

  override def getCacheKey = "db.forums"

}