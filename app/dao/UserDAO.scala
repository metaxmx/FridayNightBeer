package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.{ BaseModelSpec, User }

@Singleton
class UserDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericNumericKeyDAO[User] with ReactiveMongoComponents with BaseModelComponents[User, Int] {

  override def spec: BaseModelSpec[User, Int] = implicitly

  override def getCacheKey = "db.users"

}