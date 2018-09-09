package com.fridaynightbeer.entity

/**
  * DB Entity describing a user.
  *
  * @param loginName    the login name
  * @param passwordHash the password hash
  * @param firstName    the first name of the user
  * @param lastName     the last name of the user
  * @param email        the email address
  * @param id           the database id
  */
case class UserEntity(loginName: String,
                      passwordHash: String,
                      firstName: Option[String],
                      lastName: Option[String],
                      email: String,
                      id: String) extends KeyedEntity {

  def matchesPassword(password: String): Boolean = passwordHash == password // TODO: Hashing, Salt

}

object UserEntity extends ((String, String, Option[String], Option[String], String, String) => UserEntity) {

  def build(loginName: String,
            passwordHash: String,
            firstName: Option[String],
            lastName: Option[String],
            email: String): UserEntity = {
    new UserEntity(loginName, passwordHash, firstName, lastName, email, KeyedEntity.uniqueId())
  }

}
