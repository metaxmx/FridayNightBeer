package storage

import models.{User, UserSession}
import util.FutureOption

import scala.concurrent.Future

/**
  * DAO for session.
  */
trait SessionDAO extends GenericDAO[UserSession] {

  /**
    * Change assigned user of existing session.
    * @param id session id
    * @param userOpt user option to assign to user
    * @return future-option of the changed user session
    */
  def updateSessionUser(id: String, userOpt: Option[User]): FutureOption[UserSession]

}