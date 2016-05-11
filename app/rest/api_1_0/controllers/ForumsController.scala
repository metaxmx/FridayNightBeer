package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import controllers.SecuredController
import play.api.mvc.Controller
import services.{PermissionService, PostService, _}

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

}
