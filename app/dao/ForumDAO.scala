package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.Forum

@Singleton
class ForumDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends GenericNumericKeyDAO[Forum] with ReactiveMongoComponents {

  override def getCacheKey = "db.forums"

}