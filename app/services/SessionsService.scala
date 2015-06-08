package services

import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton
import models.{ FnbSession, User }
import play.api.Logger
import exceptions.QueryException
import reactivemongo.bson.BSONObjectID
import play.cache.Cache
import scala.util.Success

@Singleton
class SessionsService {

  val CACHE_INTERVAL_SESSION = 10 * 60

  def sessionsCollection: JSONCollection = db.collection[JSONCollection]("sessions")

  def cachekeySession(id: String) = s"db.session.$id"

  def selectSessionById(id: String) = sessionsCollection.find(Json.obj("id_" -> id))

  def findSession(id: String): Future[Option[FnbSession]] =
    CacheUtil.getOrElseAsync(cachekeySession(id), findSessionFromDb(id), CACHE_INTERVAL_SESSION)

  def findSessionFromDb(id: String): Future[Option[FnbSession]] = try {
    Logger.info(s"Fetching Session $id from database")
    selectSessionById(id).one[FnbSession]
  } catch {
    case thr: Throwable => throw new QueryException("Error finding session", thr)
  }

  def insertSession(session: FnbSession) = {
    sessionsCollection.insert(session) recover {
      case exc => {
        Logger.error(s"Error inserting session: ${exc.getMessage}")
        throw new QueryException(exc)
      }
    }
  }

  def updateSessionUser(id: String, userOpt: Option[User]) = {
    findSession(id) flatMap {
      case None => {
        Logger.warn(s"Cannot login/logout: Session $id not found.")
        Future.failed(QueryException("Session not found"))s
      }
      case Some(session) => {
        val userId = userOpt map { _._id stringify }
        userOpt.fold {
          Logger.info(s"Logging out session")
        } {
          user => Logger.info(s"Logging in session as user ${user.username}")
        }

        val sessionUpdated = session.withUser(None)
        sessionsCollection.update(Json.obj("id_" -> id), sessionUpdated) andThen {
          case Success(_) => Cache.set(cachekeySession(id), sessionUpdated, CACHE_INTERVAL_SESSION)
        }

      }
    }
  }

}