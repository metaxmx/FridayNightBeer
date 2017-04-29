package storage

import models.User

/**
  * DAO for users.
  */
trait UserDAO extends GenericDAO[User]