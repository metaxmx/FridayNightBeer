package controllers

import play.api.mvc._
import play.api.mvc.Results._
import models.User
import javax.inject.Inject
import scala.concurrent.Future
import play.Logger
import scala.concurrent.ExecutionContext
import models.FnbSession
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import services.{UsersService, SessionsService}

trait Secured {

  def sessionCollectionx: JSONCollection = db.collection[JSONCollection]("sessions")

  def parseSessionKey(request: RequestHeader) = request.headers.get("x-fnb-session")

  def onMissingSession(request: RequestHeader) = {
    Logger.warn("Returning API request without session header")
    BadRequest("unauthorized")
  }

  def onSessionLoadingError[A](request: Request[A]) = {
    Logger.error("Error reading Session from DB")
    InternalServerError("session not found")
  }

  def withSessionKeyAsync[A](block: => String => Request[A] => Future[Result]): Request[A] => Future[Result] =
    request => parseSessionKey(request) match {
      case None          => Future.successful(onMissingSession(request))
      case Some(session) => block(session)(request)
    }

  def withSessionKey[A](block: => String => Request[A] => Result): Request[A] => Result =
    request => parseSessionKey(request) match {
      case None          => onMissingSession(request)
      case Some(session) => block(session)(request)
    }

  def withSession[A](block: => Tuple2[FnbSession, Option[User]] => Request[A] => Future[Result])(implicit usersService: UsersService, sessionsService: SessionsService): Request[A] => Future[Result] =
    request =>
      parseSessionKey(request) match {
        case None => Future.successful(onMissingSession(request))
        case Some(sessionKey) =>
          sessionsService.findSession(sessionKey)
          sessionCollection.find(Json.obj("sessionkey" -> sessionKey)).one[FnbSession] flatMap {
            case None => Future.successful(None)
            case Some(session) => session.user_id match {
              case None => Future.successful(Some(Tuple2(session, None)))
              case Some(userId) => usersService.findUser(userId) map {
                _ map { user => Tuple2(session, Some(user)) }
              }
            }
          } flatMap {
            case None        => Future.successful(onSessionLoadingError(request))
            case Some(tuple) => block(tuple)(request)
          }
      }

}