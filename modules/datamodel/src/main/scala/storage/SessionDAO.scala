package storage

import models.{User, UserSession}

import scala.concurrent.Future

trait SessionDAO extends GenericDAO[UserSession] {

  def updateSessionUser(id: String, userOpt: Option[User]): Future[Option[UserSession]]

}