package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.{ BaseModelSpec, ForumCategory }

@Singleton
class ForumCategoryDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericNumericKeyDAO[ForumCategory] with ReactiveMongoComponents with BaseModelComponents[ForumCategory, Int] {

  override def spec: BaseModelSpec[ForumCategory, Int] = implicitly

  override def getCacheKey = "db.categories"

}