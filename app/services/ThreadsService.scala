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
import reactivemongo.api.ReadPreference.Primary
import exceptions.QueryException
import reactivemongo.api.commands.GetLastError
import reactivemongo.core.errors.DatabaseException
import reactivemongo.api.commands.DefaultWriteResult
import util.GenericDAO
import util.EntityName
import util.TypedCache
import util.TypedSingletonCache

@Singleton
class ThreadsService extends GenericDAO[Thread] {

  implicit val entityNames = EntityName("thread")
  
  val CACHE_INTERVAL_THREAD = 10 * 60

  def cachekeyThread(id: String) = s"db.thread.$id"

  val cacheThread = new TypedCache[Thread](_._id.toString, cachekeyThread, CACHE_INTERVAL_THREAD)

  val cacheAllThreadsByForum = new TypedSingletonCache[Map[Int, Seq[Thread]]]("db.threads", CACHE_INTERVAL_THREAD)

  implicit def threadsCollection = db.collection[BSONCollection](Thread.collectionName)

  def findThreadsByForumFromDb: Future[Map[Int, Seq[Thread]]] = findAll map { _.groupBy { _.forum } }

  def getThreadsByForum: Future[Map[Int, Seq[Thread]]] = cacheAllThreadsByForum.getOrElseAsyncDef(findThreadsByForumFromDb)

  def insertThread(thread: Thread): Future[Thread] =
    insertOptimistic(thread) andThen {
      case _ => cacheAllThreadsByForum.remove
    }

}