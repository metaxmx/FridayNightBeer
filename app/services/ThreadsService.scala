package services

import javax.inject.Singleton
import models.Thread
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import reactivemongo.api.ReadPreference
import exceptions.QueryException

@Singleton
class ThreadsService {

  val CACHE_INTERVAL_THREAD = 10 * 60

  def cachekeyThread(id: String) = s"db.thread.$id"

  val cacheThread = new TypedCache[Thread](_._id.toString, cachekeyThread, CACHE_INTERVAL_THREAD)

  val cacheAllThreadsByForum = new TypedSingletonCache[Map[Int, Seq[Thread]]]("db.threads", CACHE_INTERVAL_THREAD)

  def threadsCollection = db.collection[BSONCollection](Thread.collectionName)

  def findThreadsFromDb: Future[Seq[Thread]] = {
    Logger.info(s"Fetching Threads from database")
    threadsCollection.find(BSONDocument()).cursor[Thread](ReadPreference.Primary).collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading threads", exc)
        throw new QueryException("Error loading threads", exc)
      }
    }
  }

  def findThreadsByForumFromDb: Future[Map[Int, Seq[Thread]]] = findThreadsFromDb map { _.groupBy { _.forum } }

  def getThreadsByForum: Future[Map[Int, Seq[Thread]]] = cacheAllThreadsByForum.getOrElseAsyncDef(findThreadsByForumFromDb)

}