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
import scala.util.Success

@Singleton
class UsersService {

  case class UsernameData(username: String, userId: Int)

  val CACHE_INTERVAL_USER = 10 * 60

  val CACHE_INTERVAL_USERNAME = 24 * 60 * 60

  def cachekeyUser(id: String) = s"db.user.$id"

  def cachekeyUsername(username: String) = s"db.username.$username"

  val cacheUser = new TypedCache[User](_._id.toString, cachekeyUser, CACHE_INTERVAL_USER)

  val cacheUsername = new TypedCache[UsernameData](_.username, cachekeyUsername, CACHE_INTERVAL_USERNAME)

  def usersCollection: JSONCollection = db.collection[JSONCollection]("users")

  def selectUserById(id: Int) = usersCollection.find(Json.obj("_id" -> id))

  def selectUserByUsername(username: String) = usersCollection.find(Json.obj("username" -> username.toLowerCase))

  def findUser(id: Int): Future[Option[User]] = cacheUser.getOrElseAsync(id.toString, findUserFromDb(id))

  def findUserByUsername(username: String): Future[Option[User]] =
    cacheUsername.get(username) match {
      case Some(usernameData) => findUser(usernameData.userId)
      case None => findUserByUsernameFromDb(username) andThen {
        case Success(Some(user)) => {
          Logger.info(s"username: $username, cache key: ${cachekeyUsername(username)}, value: ${user._id}")
          cacheUsername.set(UsernameData(username, user._id))
          cacheUser.set(user)
        }
      }
    }

  def findUserFromDb(id: Int): Future[Option[User]] = {
    Logger.info(s"Fetching User $id from database")
    selectUserById(id).one[User] recover {
      case exc => {
        Logger.error("Error finding user", exc)
        throw new QueryException("Error finding user", exc)
      }
    }
  }

  def findUserByUsernameFromDb(username: String): Future[Option[User]] = {
    Logger.info(s"Fetching User with username $username from database")
    selectUserByUsername(username).one[User] recover {
      case exc => {
        Logger.error("Error finding user by username", exc)
        throw new QueryException("Error finding user by username", exc)
      }
    }
  }

}