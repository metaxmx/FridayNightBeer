package dao

import javax.inject.Singleton

import models.Thread

@Singleton
class ThreadDAO extends GenericNumericKeyDAO[Thread] {

  override def getCacheKey = "db.threads"

}