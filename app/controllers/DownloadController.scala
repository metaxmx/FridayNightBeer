package controllers

import java.io.File

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import play.api.Logger
import play.api.mvc.{ Action, AnyContent, Controller }

import services.{ SessionService, UserService }

@Singleton
class DownloadController @Inject() (implicit userService: UserService,
                                    sessionsService: SessionService) extends Controller with Secured {

  def downloadAvatar(id: Int) = Action.async {
    withSession[AnyContent] {
      sessionInfo =>
        request =>
          sessionInfo.userOpt.fold {
            Future.successful(Forbidden("access denied"))
          } {
            _ =>
              userService.getUser(id).map {
                _.filter { _.avatar.isDefined }.map {
                  userOpt =>
                    Logger info s"File: appdata/avatars/" + userOpt.avatar.get
                    new File("appdata/avatars/" + userOpt.avatar.get)
                }.filter { _.exists }.fold {
                  NotFound("not found")
                } {
                  file => Ok.sendFile(content = file, inline = true)
                }
              }
          }
    }
  }

}