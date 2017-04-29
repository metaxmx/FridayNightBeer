package storage

import models.Post

/**
  * DAO for forum posts.
  */
trait PostDAO extends GenericDAO[Post]