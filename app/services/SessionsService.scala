package services

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton
import models.{ FnbSession, User }
import play.api.Logger
import exceptions.QueryException
import reactivemongo.bson.BSONObjectID
import scala.util.Success
import scala.util.Failure

@Singleton
class SessionsService {

  val CACHE_INTERVAL_SESSION = 10 * 60

  val cacheSession = new TypedCache[FnbSession](_._id, cachekeySession, CACHE_INTERVAL_SESSION)

  def sessionsCollection = db.collection[BSONCollection]("sessions")

  def cachekeySession(id: String) = s"db.session.$id"

  def selectSessionById(id: String) = sessionsCollection.find(BSONDocument("_id" -> id)).one[FnbSession]

  def findSession(id: String): Future[Option[FnbSession]] =
    cacheSession.getOrElseAsync(id, findSessionFromDb(id))

  def findSessionFromDb(id: String): Future[Option[FnbSession]] = {
    Logger.info(s"Fetching Session $id from database")
    selectSessionById(id) recover {
      case exc => {
        Logger.error("Error finding session", exc)
        throw new QueryException("Error finding session", exc)
      }
    }
  }

  def insertSession(session: FnbSession): Future[FnbSession] = {
    sessionsCollection.insert(session) map (_ => session) andThen {
      case Success(_) => cacheSession.set(session)
    } recover {
      case exc => {
        Logger.error("Error inserting session", exc)
        throw new QueryException("Error inserting session", exc)
      }
    }
  }

  def updateSessionUser(id: String, userOpt: Option[User]): Future[FnbSession] = {
    findSession(id) flatMap {
      case None => {
        Logger.warn(s"Cannot login/logout: Session $id not found.")
        Future.failed(QueryException("Session not found"))
      }
      case Some(session) => {
        val userId = userOpt map { _._id.toString }
        userOpt.fold {
          Logger.info(s"Logging out session")
        } {
          user => Logger.info(s"Logging in session as user ${user.username}")
        }

        val sessionUpdated = session.withUser(userOpt map { _._id })
        // TODO: Does insert work here???
        sessionsCollection.insert(sessionUpdated) map {
          writeResult =>
            {
              Logger.info(s"Result of Update: ${writeResult.n} for object $sessionUpdated")
              cacheSession.set(sessionUpdated)
              sessionUpdated
            }
        } recover {
          case exc => {
            Logger.error("Error updating session", exc)
            throw new QueryException("Error updating session", exc)
          }
        }

      }
    }
  }

}