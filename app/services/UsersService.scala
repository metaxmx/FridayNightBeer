package services

import models.User
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton
import reactivemongo.api.ReadPreference
import exceptions.QueryException
import play.api.Logger
import play.cache.Cache
import scala.util.Success

@Singleton
class UsersService {

  val CACHE_INTERVAL_USER = 10 * 60

  val CACHE_INTERVAL_USERNAME = 24 * 60 * 60

  def usersCollection: JSONCollection = db.collection[JSONCollection]("users")

  def cachekeyUser(id: String) = s"db.user.$id"

  def cachekeyUsername(username: String) = s"db.username.$username"

  def selectUserById(id: String) = usersCollection.find(Json.obj("id_" -> id))

  def selectUserByUsername(username: String) = usersCollection.find(Json.obj("username" -> username))

  def findUser(id: String): Future[Option[User]] =
    CacheUtil.getOrElseAsync(cachekeyUser(id), findUserFromDb(id), CACHE_INTERVAL_USER)

  def findUserByUsername(username: String): Future[Option[User]] = {
    val cachedUserId = Option(Cache.get(cachekeyUsername(username)).asInstanceOf[String])
    cachedUserId match {
      case Some(userId) => findUser(userId)
      case None => {
        findUserByUsernameFromDb(username) andThen {
          case Success(Some(user)) => {
            Cache.set(cachekeyUsername(username), user.username, CACHE_INTERVAL_USERNAME)
            Cache.set(cachekeyUser(user._id.stringify), user, CACHE_INTERVAL_USER)
          }
        }
      }
    }
  }

  def findUserFromDb(id: String): Future[Option[User]] = try {
    Logger.info(s"Fetching User $id from database")
    selectUserById(id).one[User]
  } catch {
    case thr: Exception => throw new QueryException("Error finding user", thr)
  }

  def findUserByUsernameFromDb(username: String): Future[Option[User]] = try {
    Logger.info(s"Fetching User with username $username from database")
    selectUserByUsername(username).one[User]
  } catch {
    case thr: Exception => throw new QueryException("Error finding user by username", thr)
  }

}