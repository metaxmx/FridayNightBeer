package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models.User
import permissions.{Authorization, GlobalPermissions}
import permissions.GlobalPermissions.Forums
import play.api.libs.json.Json._
import services.{PermissionService, PostService, _}
import rest.api_1_0.viewmodels.ForumsViewModels._

import scala.concurrent.Future

/**
  * Created by Christian Simon on 11.05.2016.
  */
@Singleton
class ForumsController @Inject() (implicit val userService: UserService,
                                  val sessionService: SessionService,
                                  forumService: ForumService,
                                  forumCategoryService: ForumCategoryService,
                                  threadService: ThreadService,
                                  postService: PostService,
                                  val permissionService: PermissionService) extends RestController {

  def getForums = OptionalSessionRestAction.async {
    implicit request =>
      getForumInfo map { Ok apply _ }
  }

  private def getForumInfo(implicit authorization: Authorization): Future[ForumInfoResult] =
    for {
      _ <- requirePermissions(GlobalPermissions.Forums)
      categories <- forumCategoryService.getCategories
      forums <- forumService.getForumsByCategory
      threads <- threadService.getThreadsByForum
      userIndex <- userService.getUserIndex
    } yield createForumInfo(categories, forums, threads, userIndex)

}
