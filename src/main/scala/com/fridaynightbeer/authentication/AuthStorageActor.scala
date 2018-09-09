package com.fridaynightbeer.authentication

import akka.actor.{Actor, Props}

class AuthStorageActor extends Actor {

  import AuthStorageActor._

  def receiveState(sessions: Map[String, Session]): Receive = {
    case CreateSession(session) =>
      context.become(receiveState(sessions + (session.sessionId -> session)))
      sender ! CreatedSession(session)
    case FindSession(sessionId) =>
      sessions.get(sessionId) match {
        case Some(session) => sender ! SessionFound(session)
        case None => sender ! SessionNotFound(sessionId)
      }
    case DestroySession(sessionId) =>
      context.become(receiveState(sessions - sessionId))
      sender ! SessionDestroyed(sessionId)
  }

  override def receive: Receive = receiveState(Map.empty)

}

object AuthStorageActor {

  def apply(): Props = Props(new AuthStorageActor())

  case class CreateSession(session: Session)

  case class CreatedSession(session: Session)

  case class FindSession(sessionId: String)

  sealed trait FindSessionResult

  case class SessionNotFound(sessionId: String) extends FindSessionResult

  case class SessionFound(session: Session) extends FindSessionResult

  case class DestroySession(sessionId: String)

  case class SessionDestroyed(sessionId: String)


}
