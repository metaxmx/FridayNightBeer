package storage

import models.{AccessRule, Permission}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 05.05.2016.
  */
trait PermissionDAO extends GenericDAO[Permission] {

  def getPermissionMap: Future[Map[String, Map[String, AccessRule]]] = getAll map {
    _.groupBy(_.permissionType).mapValues(_.groupBy(_.permission).mapValues(_.head.accessRule))
  }

}