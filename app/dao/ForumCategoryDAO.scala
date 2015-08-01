package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.ForumCategory

@Singleton
class ForumCategoryDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericNumericKeyDAO[ForumCategory] with ReactiveMongoComponents {

  override def getCacheKey = "db.categories"

}