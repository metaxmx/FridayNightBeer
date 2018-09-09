package com.fridaynightbeer.db

import com.fridaynightbeer.db.slick.UserDaoImpl
import com.fridaynightbeer.entity.UserEntity

import scala.concurrent.Future

/**
  * Database Access Object for User Entity.
  */
trait UserDao extends GenericDao[UserEntity] {

  /**
    * Find a user by their login name.
    *
    * @param login login name
    * @return optional found user
    */
  def findByLogin(login: String): Future[Option[UserEntity]]

}

object UserDao {

  def apply(): UserDao = UserDaoImpl.globalUserDao

}