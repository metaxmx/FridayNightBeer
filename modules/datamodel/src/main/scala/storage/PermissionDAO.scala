package storage

import models.{AccessRule, Permission}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import PermissionDAO.PermissionMap

/**
  * Created by Christian Simon on 05.05.2016.
  */
trait PermissionDAO extends GenericDAO[Permission] {

  def getPermissionMap: Future[PermissionMap] = getAll map {
    _.groupBy(_.permissionType).mapValues(_.groupBy(_.permission).mapValues(_.head.accessRule))
  }

}

object PermissionDAO {

  type PermissionMap = Map[String, Map[String, AccessRule]]

}