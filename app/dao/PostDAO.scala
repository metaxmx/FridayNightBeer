package dao

import javax.inject.Singleton

import models.Post

@Singleton
class PostDAO extends GenericNumericKeyDAO[Post] {

  override def getCacheKey = "db.posts"

}