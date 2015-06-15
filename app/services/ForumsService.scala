package services

import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Singleton
import scala.concurrent.Future
import dtos.ListForumsDTO
import play.api.Logger
import exceptions.QueryException
import models.Forum
import models.ForumCategory

@Singleton
class ForumsService {

  val CACHE_INTERVAL_FORUMS = 10 * 60

  val cacheListForums = new TypedSingletonCache[ListForumsDTO]("db.listforums", CACHE_INTERVAL_FORUMS)

  def forumsCollection: JSONCollection = db.collection[JSONCollection]("forums")

  def categoriesCollection: JSONCollection = db.collection[JSONCollection]("categories")

  def getForumsDTO: Future[ListForumsDTO] = cacheListForums.getOrElseAsyncDef(fetchForumsDTO)

  def fetchForumsDTO: Future[ListForumsDTO] =
    findForumsFromDb flatMap {
      forums => findCategoriesFromDb map { (forums, _) }
    } map {
      case (forums, categories) => ListForumsDTO.createFromModels(categories, forums)
    }

  def findForumsFromDb: Future[Seq[Forum]] = {
    Logger.info(s"Fetching Forums from database")
    forumsCollection.find(Json.obj()).cursor[Forum].collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading forums", exc)
        throw new QueryException("Error loading forums", exc)
      }
    }
  }

  def findCategoriesFromDb: Future[Seq[ForumCategory]] = {
    Logger.info(s"Fetching Forum Categories from database")
    forumsCollection.find(Json.obj()).cursor[ForumCategory].collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading forum categories", exc)
        throw new QueryException("Error loading forum categories", exc)
      }
    }
  }

}