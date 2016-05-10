package permissions

import models.User

/**
  * Abstract security principal.
  * Created by Christian on 10.05.2016.
  */
trait AuthorizationPrincipal {

  /**
    * Optional User as authorization principal.
    */
  val userOpt: Option[User]

}
