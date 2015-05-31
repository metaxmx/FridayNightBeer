package controllers

import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import scala.concurrent.Future
import reactivemongo.api.Cursor
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.slf4j.{ LoggerFactory, Logger }
import javax.inject.Singleton
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json.toJson
import dtos.AuthInfoDTO

@Singleton
class Authentication extends Controller with MongoController with Secured {

  def getAuthInfo = Action.async {
    withSession[AnyContent] {
      authInfo =>
        request =>
          val result = authInfo match {
            case Tuple2(session, None)       => AuthInfoDTO(false, null, null, null)
            case Tuple2(session, Some(user)) => AuthInfoDTO(true, user._id.toString(), user.displayName, user.username)
          }
          Future.successful(Ok(toJson(result)).as("application/json"))
    }
  }

}