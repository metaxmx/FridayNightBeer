package controllers

import java.io.File
import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.Logger
import play.api.mvc.{ Action, AnyContent, Controller }
import services.{ SessionService, UserService }
import services.PostService
import services.ThreadService
import services.ForumService
import models._

@Singleton
class DownloadController @Inject() (implicit val userService: UserService,
                                    val sessionService: SessionService,
                                    postService: PostService,
                                    threadService: ThreadService,
                                    forumService: ForumService) extends Controller with Secured {

  def downloadAvatar(id: Int) = UserAction.async {
    request =>
      userService.getUser(id).map {
        _.filter { _.avatar.isDefined }.map {
          userOpt =>
            Logger.info(s"File: appdata/avatars/${userOpt.avatar.get}")
            new File(s"appdata/avatars/${userOpt.avatar.get}")
        }.filter { _.exists }.fold {
          NotFound("not found")
        } {
          file => Ok.sendFile(content = file, inline = true)
        }
      }
  }

  def downloadPostUpload(id: Int, filename: String) = OptionalSessionAction.async {
    request =>
      implicit val userOpt = request.maybeUser
      for {
        post <- postService.getPostForApi(id)
        thread <- threadService.getThreadForApi(post.thread)
        forum <- forumService.getForumForApi(thread.forum)
      } yield {
        val postId = post._id
        val uploadOpt = post.uploads.find(_.filename == filename)
        val fileOpt = uploadOpt map {
          upload =>
            Logger.info(s"appdata/uploads/$postId/${upload.source}")
            new File(s"appdata/uploads/$postId/${upload.source}")
        } filter { _.exists }
        fileOpt.fold {
          NotFound("not found")
        } {
          file =>
            Ok.sendFile(content = file, fileName = (_ => filename))
        }
      }

  }

}