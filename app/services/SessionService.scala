package services

import javax.inject.{Inject, Singleton}

import models.{User, UserSession}
import storage.SessionDAO
import util.FutureOption

import scala.concurrent.Future

@Singleton
class SessionService @Inject()(uuidGenerator: UUIDGenerator,
                               sessionDAO: SessionDAO) {

  def getSession(id: String): FutureOption[UserSession] = sessionDAO ?? id

  def insertSession(session: UserSession): Future[UserSession] = sessionDAO <<! session

  def createSession(userOpt: Option[User]): Future[UserSession] = sessionDAO <<! UserSession(uuidGenerator.generate.toString, userOpt map (_._id))

  def updateSessionUser(id: String, userOpt: Option[User]): FutureOption[UserSession] = sessionDAO.updateSessionUser(id, userOpt)

  def removeSession(id: String): Future[Boolean] = sessionDAO.remove(id)

}