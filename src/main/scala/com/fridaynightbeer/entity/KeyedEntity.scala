package com.fridaynightbeer.entity

import java.util.UUID

/**
  * General Entity having an ID (UUID String).
  */
trait KeyedEntity {

  /**
    * THe UUID / Primary key of the database entity.
    * @return ID
    */
  def id: String

}

object KeyedEntity {

  /**
    * Create new Unique ID.
    * @return created id
    */
  def uniqueId(): String = UUID.randomUUID().toString

}