package storage.mongo

import java.util.UUID

import org.scalatest.{BeforeAndAfterAll, WordSpec}
import play.api.cache.CacheApi
import play.api.inject.DefaultApplicationLifecycle
import play.api.{Configuration, Environment}
import play.modules.reactivemongo.{DefaultReactiveMongoApi, ReactiveMongoApi}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Integration Test Spec for DAOs implemented with MongoDB.
  */
abstract class MongoDAOTestSpec extends WordSpec with BeforeAndAfterAll {

  protected def buildCacheApi(): CacheApi = new InMemoryCacheApi

  protected def uuid(): String = UUID.randomUUID().toString

  private val configuration: Configuration = Configuration.load(Environment.simple())

  private val testLifeCycle = new DefaultApplicationLifecycle

  private val shutDownTimeout: Duration = 5.seconds

  protected def clearTestCollections: Boolean = true

  protected val mongoApi = new DefaultReactiveMongoApi(configuration, testLifeCycle)

  override def afterAll(): Unit = {
    Await.ready(testLifeCycle.stop(), shutDownTimeout)
  }

  trait MongoFixture {

    def withMongoDatabase[DAO <: MongoGenericDAO[_]](daoBuilder: (CacheApi, ReactiveMongoApi, Option[String]) => DAO)
                                                    (block: DAO => Unit): Unit = {
      val dbCollectionSuffix = Some("_" + uuid())
      val dao = daoBuilder(buildCacheApi(), mongoApi, dbCollectionSuffix)
      try {
        block(dao)
      } finally {
        if (clearTestCollections) {
          val dropFuture = dao.collectionFuture.flatMap(_.drop(failIfNotFound = false))
          Await.ready(dropFuture, shutDownTimeout)
        }
      }
    }

  }

}
