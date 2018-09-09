package com.fridaynightbeer.db.slick

import com.fridaynightbeer.FridayNightBeer
import com.fridaynightbeer.db.UserDao
import com.fridaynightbeer.entity.UserEntity
import com.fridaynightbeer.entity.slick.UserTable
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class UserDaoImpl(implicit db: Database, ec: ExecutionContext)
  extends GenericDaoImpl[UserEntity, UserTable](UserTable()) with UserDao {

  override def findByLogin(login: String): Future[Option[UserEntity]] =
    db.run(this.findBy(_.loginName).applied(login).result.headOption)

}

object UserDaoImpl {

  lazy val globalUserDao: UserDaoImpl = UserDaoImpl()

  def apply()(implicit db: Database = FridayNightBeer.db,
              ec: ExecutionContext = FridayNightBeer.system.dispatcher): UserDaoImpl = new UserDaoImpl()

}