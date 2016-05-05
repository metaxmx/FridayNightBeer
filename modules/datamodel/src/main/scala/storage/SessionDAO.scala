package storage

import models.{User, UserSession}
import util.FutureOption

import scala.concurrent.Future

trait SessionDAO extends GenericDAO[UserSession] {

  def updateSessionUser(id: String, userOpt: Option[User]): FutureOption[UserSession]

}