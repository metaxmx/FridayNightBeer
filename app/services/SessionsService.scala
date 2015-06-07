package services

import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton
import models.FnbSession
import play.api.Logger
import exceptions.QueryException

@Singleton
class SessionsService {

  def sessionsCollection: JSONCollection = db.collection[JSONCollection]("sessions")

  def cachekeySession(id: String) = s"db.session.$id"

  def selectSessionById(id: String) = sessionsCollection.find(Json.obj("id_" -> id))

  def findSession(id: String): Future[Option[FnbSession]] =
    CacheUtil.getOrElseAsync(cachekeySession(id), findSessionFromDb(id), 10 * 60)

  def findSessionFromDb(id: String): Future[Option[FnbSession]] = try {
    Logger.info(s"Fetching Session $id from database")
    selectSessionById(id).one[FnbSession]
  } catch {
    case thr: Throwable => throw new QueryException("Error finding session", thr)
  }

}