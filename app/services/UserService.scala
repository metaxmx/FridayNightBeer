package services

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import exceptions.ApiExceptions.dbException
import exceptions.QueryException
import models.User
import storage.UserDAO

@Singleton
class UserService @Inject() (userDAO: UserDAO) {

  def getUser(id: String): Future[Option[User]] = userDAO ?? id

  def getUserByUsername(username: String): Future[Option[User]] = userDAO >> { _ find { _.username equals username } }

  def getUsers: Future[Seq[User]] = userDAO.getAll

  def getUserIndex: Future[Map[String, User]] = userDAO.getMap

  def getUserForApi(id: String): Future[Option[User]] = getUser(id) recover {
    case e: QueryException => dbException(e)
  }

  def getUserIndexForApi = getUserIndex recover {
    case e: QueryException => dbException(e)
  }

}