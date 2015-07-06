package services

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Singleton
import scala.concurrent.Future
import play.api.Logger
import exceptions.QueryException
import models.Forum
import models.ForumCategory
import reactivemongo.api.ReadPreference

@Singleton
class ForumsService {

  val CACHE_INTERVAL_FORUMS = 10 * 60

  def cachekeyForum(id: String) = s"db.forum.$id"

  val cacheListForums = new TypedSingletonCache[ForumsAndCategories]("dto.listforums", CACHE_INTERVAL_FORUMS)

  val cacheForum = new TypedCache[Forum](_._id.toString, cachekeyForum, CACHE_INTERVAL_FORUMS)

  def forumsCollection = db.collection[BSONCollection]("forums")

  def categoriesCollection = db.collection[BSONCollection]("categories")

  def getForumsAndCategories: Future[ForumsAndCategories] = cacheListForums.getOrElseAsyncDef(fetchForumsAndCategories)

  def fetchForumsAndCategories: Future[ForumsAndCategories] = findForumsFromDb flatMap {
    forums => findCategoriesFromDb map { ForumsAndCategories(_, forums) }
  }

  def findForum(id: Int): Future[Option[Forum]] = cacheForum.getOrElseAsync(id.toString, findForumFromDb(id))

  def findForumsFromDb: Future[Seq[Forum]] = {
    Logger.info(s"Fetching Forums from database")
    forumsCollection.find(BSONDocument()).cursor[Forum](ReadPreference.Primary).collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading forums", exc)
        throw new QueryException("Error loading forums", exc)
      }
    }
  }

  def findCategoriesFromDb: Future[Seq[ForumCategory]] = {
    Logger.info(s"Fetching Forum Categories from database")
    categoriesCollection.find(BSONDocument()).cursor[ForumCategory](ReadPreference.Primary).collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading forum categories", exc)
        throw new QueryException("Error loading forum categories", exc)
      }
    }
  }

  def findForumFromDb(id: Int): Future[Option[Forum]] = {
    Logger.info(s"Fetching Forum $id from database")
    forumsCollection.find(BSONDocument("_id" -> id)).one[Forum] recover {
      case exc => {
        Logger.error("Error finding forum", exc)
        throw new QueryException("Error finding forum", exc)
      }
    }
  }

}

case class ForumsAndCategories(categories: Seq[ForumCategory], forums: Seq[Forum])
