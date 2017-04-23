package services

import javax.inject.{Inject, Singleton}

import authentication.{PermissionAuthorization, UserProfile}
import storage.PermissionDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PermissionService @Inject()(permissionDAO: PermissionDAO) {

  def createAuthorization()(implicit userProfile: UserProfile): Future[PermissionAuthorization] = {
    for {
      permissionMap <- permissionDAO.getPermissionMap
    } yield {
      new PermissionAuthorization(userProfile, permissionMap)
    }
  }

}
