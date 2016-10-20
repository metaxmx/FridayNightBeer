package controllers

import javax.inject.{Inject, Singleton}

import services.{PermissionService, SessionService, UserService}


/**
  * Controller for User-Related Actions.
  * Created by Christian Simon on 26.05.2016.
  */
@Singleton
class UserController @Inject()(val userService: UserService,
                               val sessionService: SessionService,
                               val permissionService: PermissionService) extends RestController {

}
