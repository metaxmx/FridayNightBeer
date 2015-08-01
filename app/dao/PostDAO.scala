package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.Post

@Singleton
class PostDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends GenericNumericKeyDAO[Post] with ReactiveMongoComponents {

  override def getCacheKey = "db.posts"

}