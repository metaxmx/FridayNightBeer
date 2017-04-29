package services

import javax.inject.{Inject, Singleton}

import models.User
import storage.UserDAO
import util.FutureOption

import scala.concurrent.Future

@Singleton
class UserService @Inject()(userDAO: UserDAO) {

  def getUser(id: String): FutureOption[User] = userDAO getById id

  def getUserByUsername(username: String): FutureOption[User] = FutureOption(userDAO map {
    _ find (_.username.toLowerCase equals username.toLowerCase)
  })

  def getUserByEmail(email: String): FutureOption[User] = FutureOption(userDAO map {
    _ find (_.email.toLowerCase equals email.toLowerCase)
  })

  def getUsers: Future[Seq[User]] = userDAO.getAll

  def getUserIndex: Future[Map[String, User]] = userDAO.getMap

  def createUser(user: User) = userDAO insert user

}