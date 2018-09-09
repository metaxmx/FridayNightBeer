package com.fridaynightbeer.entity.slick

import com.fridaynightbeer.entity.KeyedEntity
import slick.jdbc.MySQLProfile.api.Table
import slick.lifted.Rep

trait KeyedTable {

  self: Table[_ <: KeyedEntity] =>

  def id: Rep[String]

}
