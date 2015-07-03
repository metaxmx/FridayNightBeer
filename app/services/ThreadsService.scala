package services

import javax.inject.Singleton
import models.Thread
import reactivemongo.api.collections.bson.BSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current

@Singleton
class ThreadsService {

   val CACHE_INTERVAL_THREAD = 10 * 60
   
   def cachekeyThread(id: String) = s"db.thread.$id"
  
  val cacheThread = new TypedCache[Thread](_._id.toString, cachekeyThread, CACHE_INTERVAL_THREAD)

  val cacheAllThreadsByForum = new TypedSingletonCache[Map[Int, Thread]]("db.threads", CACHE_INTERVAL_THREAD)
  
  def threadsCollection = db.collection[BSONCollection](Thread.collectionName)
   
}