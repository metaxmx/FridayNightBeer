package storage

import models.{AccessRule, Permission}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import PermissionDAO.PermissionMap

/**
  * DAO for permission.
  */
trait PermissionDAO extends GenericDAO[Permission] {

  /**
    * Get map of global permissions.
    * @return future of permission map
    */
  def getPermissionMap: Future[PermissionMap] = getAll map {
    _.groupBy(_.permissionType).mapValues(_.groupBy(_.permission).mapValues(_.head.accessRule))
  }

}

object PermissionDAO {

  /**
    * Map from: permission type -> permission -> access rule
    */
  type PermissionMap = Map[String, Map[String, AccessRule]]

}