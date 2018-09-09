package com.fridaynightbeer.authentication

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.fridaynightbeer.authentication.AuthStorageActor._
import com.fridaynightbeer.entity.UserEntity
import com.fridaynightbeer.util.Logging
import com.fridaynightbeer.{FridayNightBeer, Settings}

import scala.concurrent.Future

class AuthenticationStorage(settings: Settings,
                            storageActorProps: Props)
                           (implicit system: ActorSystem = FridayNightBeer.system) extends Logging {

  import system.dispatcher

  private implicit val defaultTimeout: Timeout = settings.Actors.defaultTimeout

  val storageActor: ActorRef = {
    logger.debug("Starting Session Storage actor")
    system.actorOf(storageActorProps, "SessionStorage")
  }

  def findAndUpdateSession(sessionId: String): Future[Option[Session]] = {
    (storageActor ? FindSession(sessionId)).mapTo[FindSessionResult] map {
      case SessionFound(session) => Some(session)
      case SessionNotFound(_) => None
    }
  }

  def createUnauthenticatedSession(globalPermissions: Set[String]): Future[Session] = {
    val sessionId = Session.createSessionId()
    val session = UnauthenticatedSession(sessionId, globalPermissions)
    (storageActor ? CreateSession(session)).mapTo[CreatedSession].map(_.session)
  }

  def createSession(sessionId: String, user: UserEntity, globalPermissions: Set[String]): Future[Session] = {
    val session = AuthenticatedSession(sessionId, user, globalPermissions)
    (storageActor ? CreateSession(session)).mapTo[CreatedSession].map(_.session)
  }

  def createSession(user: UserEntity, globalPermissions: Set[String]): Future[Session] = {
    createSession(Session.createSessionId(), user, globalPermissions)
  }

}

object AuthenticationStorage {

  val globalAuthStorage: AuthenticationStorage = AuthenticationStorage()

  def apply(settings: Settings = FridayNightBeer.settings,
            storageActorProps: Props = AuthStorageActor())
           (implicit system: ActorSystem = FridayNightBeer.system): AuthenticationStorage = {
    new AuthenticationStorage(settings, storageActorProps)
  }

}
