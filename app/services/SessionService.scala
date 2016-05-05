package services

import javax.inject.{Inject, Singleton}

import exceptions.ApiExceptions.dbException
import exceptions.QueryException
import models.{User, UserSession}
import storage.SessionDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SessionService @Inject()(uuidGenerator: UUIDGenerator,
                               sessionDAO: SessionDAO) {

  def getSession(id: String): Future[Option[UserSession]] = sessionDAO ?? id

  def insertSession(session: UserSession): Future[UserSession] = sessionDAO <<! session

  def createSession(userOpt: Option[User]): Future[UserSession] = sessionDAO <<! UserSession(uuidGenerator.generate.toString, userOpt map (_._id))

  def updateSessionUser(id: String, userOpt: Option[User]): Future[Option[UserSession]] = sessionDAO.updateSessionUser(id, userOpt)

  def removeSession(id: String): Future[Boolean] = sessionDAO.remove(id)

  def getSessionForApi(id: String): Future[Option[UserSession]] = getSession(id) recover {
    case e: QueryException => dbException(e)
  }

}