package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.User

@Singleton
class UserDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends GenericNumericKeyDAO[User] with ReactiveMongoComponents {

  override def getCacheKey = "db.users"

}