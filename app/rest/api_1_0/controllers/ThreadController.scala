package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import permissions.GlobalPermissions
import rest.Exceptions.NotFoundException
import rest.api_1_0.viewmodels.ThreadsViewModels._
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 14.05.2016.
  */
@Singleton
class ThreadController @Inject()(val userService: UserService,
                                 val sessionService: SessionService,
                                 val permissionService: PermissionService,
                                 forumService: ForumService,
                                 forumCategoryService: ForumCategoryService,
                                 threadService: ThreadService,
                                 postService: PostService) extends RestController {

  def showThread(id: String) = OptionalSessionRestAction.async {
    implicit request =>
      mapOk(getShowThread(id))
  }

  private[this] def getShowThread(threadId: String)(implicit request: OptionalSessionRequest[_]): Future[ShowThreadResult] =
    for {
      _ <- requirePermissions(GlobalPermissions.Forums)
      thread <- threadService.getThread(threadId).flatten(throw NotFoundException(s"Thread not found: $threadId"))
      forum <- forumService.getForum(thread.forum).flatten(throw NotFoundException(s"Forum not found: ${thread.forum}"))
      category <- forumCategoryService.getCategory(forum.category).flatten(throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requirePermissionCheck(thread.checkAccess(category, forum))
      posts <- postService.getPostsForThread(threadId)
      userIndex <- userService.getUserIndex
    } yield {
      val postViewModels = posts.filter(userIndex contains _.userCreated).sorted.map {
        post =>
          val user = userIndex(post.userCreated)
          val uploads = post.uploads.map {
            upload =>
              ShowThreadPostUpload(upload.filename, upload.size, upload.hits)
          }
          ShowThreadPost(post._id, post.dateCreated, user._id, user.displayName, user.fullName, user.avatar.isDefined, post.text,
            Some(uploads).filter(_.nonEmpty))
      }
      ShowThreadResult(success = true, thread._id, thread.title, forum._id, forum.name, postViewModels)
    }

}
