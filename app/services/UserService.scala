package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import dao.UserDAO
import models.User

@Singleton
class UserService @Inject() (userDAO: UserDAO) {

  def getUser(id: Int): Future[Option[User]] = userDAO ?? id

  def getUserByUsername(username: String): Future[Option[User]] = userDAO >> { _.filter { _.username equals username }.headOption }

  def getUsers: Future[Seq[User]] = userDAO.getAll

  def getUserIndex: Future[Map[Int, User]] = userDAO.getIndex

}