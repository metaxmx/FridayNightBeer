package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.{Application, Logger}
import services._
import util.{AppSettings, FileUploadAsset}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Controller for dynamic resourced handled from the application directory.
  */
@Singleton
class AppResourceController @Inject() (val appSettings: AppSettings,
                                       val application: Application,
                                       val userService: UserService,
                                       val sessionService: SessionService,
                                       val permissionService: PermissionService,
                                       postService: PostService,
                                       forumService: ForumService,
                                       threadService: ThreadService,
                                       categoryService: ForumCategoryService) extends RestController with FileUploadAsset {

  private[this] val RES_FAVICON = "favicon.ico"
  private[this] val RES_LOGO = "logo.png"

  def favicon = handleFromAppResourcesOrDefault(RES_FAVICON)

  def logo = handleFromAppResourcesOrDefault(RES_LOGO)

  def downloadAvatar(id: String) = OptionalSessionRestAction.async {
    implicit request =>
      // TODO: Check for permissions
      userService.getUser(id).toFuture.map {
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

  def downloadPostUpload(id: String, filename: String) = OptionalSessionRestAction.async {
    implicit request =>
      val req = for {
        post <- postService.getPost(id)
        thread <- threadService.getThread(post.thread)
        forum <- forumService.getForum(thread.forum)
        category <- categoryService.getCategory(forum.category)
      } yield {
        val permissionResult = thread.checkAccess(category, forum)
        if (permissionResult) {
          val postId = post._id
          val uploadOpt = post.uploads.find(_.filename == filename)
          val fileOpt = uploadOpt map {
            upload =>
              Logger.info(s"appdata/uploads/$postId/${upload.source}")
              new File(s"appdata/uploads/$postId/${upload.source}")
          } filter {
            _.exists
          }
          fileOpt.fold {
            NotFound("not found")
          } {
            file =>
              Ok.sendFile(content = file, fileName = _ => filename)
          }
        } else {
          Forbidden("forbidden")
        }
      }
      req.flatten(NotFound("not found"))
  }

}
