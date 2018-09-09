package com.fridaynightbeer.entity.slick

import com.fridaynightbeer.entity.UserEntity
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Rep, Tag}

class UserTable(tag: Tag) extends Table[UserEntity](tag, "users") with KeyedTable {

  def loginName: Rep[String] = column[String]("login_name")

  def passwordHash: Rep[String] = column[String]("password")

  def firstName: Rep[Option[String]] = column[Option[String]]("first_name")

  def lastName: Rep[Option[String]] = column[Option[String]]("last_name")

  def email: Rep[String] = column[String]("email")

  def id: Rep[String] = column[String]("id", O.PrimaryKey)

  def * : ProvenShape[UserEntity] =
    (loginName, passwordHash, firstName, lastName, email, id) <>
      ((UserEntity.apply _).tupled, UserEntity.unapply)

}

object UserTable {

  def apply(): Tag => UserTable = new UserTable(_)

}