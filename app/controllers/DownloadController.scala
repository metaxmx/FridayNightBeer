package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger
import play.api.mvc.Controller
import services._

@Singleton
class DownloadController @Inject() (val userService: UserService,
                                    val sessionService: SessionService,
                                    val permissionsService: PermissionService,
                                    postService: PostService,
                                    threadService: ThreadService,
                                    forumService: ForumService) extends Controller with SecuredController {

  def downloadAvatar(id: String) = UserApiAction.async {
    implicit request =>
      // TODO: Check for permissions
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

  def downloadPostUpload(id: String, filename: String) = OptionalSessionApiAction.async {
    implicit request =>
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
            Ok.sendFile(content = file, fileName = _ => filename)
        }
      }

  }

}