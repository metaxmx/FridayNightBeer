package dao

import javax.inject.{ Inject, Singleton }

import play.modules.reactivemongo.{ ReactiveMongoApi, ReactiveMongoComponents }

import models.{ BaseModelSpec, Post }

@Singleton
class PostDAO @Inject() (val reactiveMongoApi: ReactiveMongoApi)
    extends GenericNumericKeyDAO[Post] with ReactiveMongoComponents with BaseModelComponents[Post, Int] {

  override def spec: BaseModelSpec[Post, Int] = implicitly

  override def getCacheKey = "db.posts"

}