package com.fridaynightbeer.entity

/**
  * DB Entity describing a user.
  *
  * @param id           the database id
  * @param loginName    the login name
  * @param passwordHash the password hash
  * @param firstName    the first name of the user
  * @param lastName     the last name of the user
  * @param email        the email address
  */
case class UserEntity(id: String,
                      loginName: String,
                      passwordHash: String,
                      firstName: Option[String],
                      lastName: Option[String],
                      email: String)
