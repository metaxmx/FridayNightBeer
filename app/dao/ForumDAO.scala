package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.{ BaseModelSpec, Forum }

@Singleton
class ForumDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericNumericKeyDAO[Forum] with ReactiveMongoComponents with BaseModelComponents[Forum, Int] {

  override def spec: BaseModelSpec[Forum, Int] = implicitly

  override def getCacheKey = "db.forums"

}