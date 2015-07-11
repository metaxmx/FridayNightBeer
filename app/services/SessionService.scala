package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import dao.SessionDAO
import models.{ User, UserSession }

@Singleton
class SessionService @Inject() (sessionDAO: SessionDAO) {

  def getSession(id: String): Future[Option[UserSession]] = sessionDAO ?? id

  def insertSession(session: UserSession): Future[UserSession] = sessionDAO <<! session

  def updateSessionUser(id: String, userOpt: Option[User]): Future[Option[UserSession]] = sessionDAO.updateSessionUser(id, userOpt)

}