package dao

import javax.inject.Singleton

import models.User

@Singleton
class UserDAO extends GenericNumericKeyDAO[User] {

  override def getCacheKey = "db.users"

  override def getCollectionName = User.collectionName

}