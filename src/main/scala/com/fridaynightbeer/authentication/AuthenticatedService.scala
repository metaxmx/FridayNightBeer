package com.fridaynightbeer.authentication

import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.model.headers.{HttpCookie, HttpCookiePair, RawHeader}
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directive0, Directive1, RequestContext}
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import com.fridaynightbeer.entity.UserEntity

import scala.util.{Failure, Success, Try}

/**
  * Service with user/session authentication
  */
trait AuthenticatedService {

  import AuthenticatedService._

  protected def authStorage: AuthenticationStorage

  private val drainRequestEntity: Directive0 = {
    extractRequestContext flatMap { ctx =>
      implicit val mat: Materializer = ctx.materializer
      // Drain request stream
      ctx.request.entity.dataBytes.runWith(Sink.cancelled)(ctx.materializer)
      pass
    }
  }

  val extractValidSession: Directive1[Session] = {
      optionalCookie(authenticationCookie) flatMap {
        case None =>
          createAndExtractSession(globalPermissions)

        case Some(HttpCookiePair(_, sessionId)) =>
          onComplete(authStorage.findAndUpdateSession(sessionId)) flatMap {
            case Failure(_) =>
              drainRequestEntity tflatMap { _ =>
                complete {
                  // TODO: Error logging
                  StatusCodes.InternalServerError -> "Could not locate session" // TODO: Better return
                }
              }
            case Success(None) =>
              respondWithHeader(RawHeader(foundInvalidSessionHeader, sessionId)) tflatMap { _ =>
                createAndExtractSession(globalPermissions)
              }
            case Success(Some(session)) => provide(session)
          }
    }
  }

  def createAndExtractSession(globalPermissions: Set[String]): Directive1[Session] = {
    onComplete(authStorage.createUnauthenticatedSession(globalPermissions)) flatMap handleSessionCreationResult
  }

  def createAndExtractAuthenticatedSession(user: UserEntity, globalPermissions: Set[String]): Directive1[Session] = {
    onComplete(authStorage.createSession(user, globalPermissions)) flatMap handleSessionCreationResult
  }

  private def handleSessionCreationResult(result: Try[Session]): Directive1[Session] = result match {
    case Failure(exception) =>
      drainRequestEntity tflatMap { _ =>
        complete {
          // TODO: Error logging
          StatusCodes.InternalServerError -> "Could not create session" // TODO: Better return
        }
      }

    case Success(session) =>
      setCookie(HttpCookie(authenticationCookie, value = session.sessionId)) tflatMap { _ =>
        respondWithHeader(RawHeader(createdSessionHeader, "true")) tflatMap { _ =>
          provide(session)
        }
      }
  }

}

object AuthenticatedService extends AuthenticatedService {

  /** TODO: Load from DB */
  val globalPermissions: Set[String] = Set.empty

  override def authStorage: AuthenticationStorage = AuthenticationStorage.globalAuthStorage

  val authenticationCookie = "fnbSessionId"

  val createdSessionHeader = "X-Created-Session"
  val foundInvalidSessionHeader = "X-Found-Invalid-Session"

}
