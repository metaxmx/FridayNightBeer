package com.fridaynightbeer.db.slick

import com.fridaynightbeer.db.GenericDao
import com.fridaynightbeer.db.slick.GenericDaoImpl.KeyedEntityTable
import com.fridaynightbeer.entity.KeyedEntity
import com.fridaynightbeer.entity.slick.KeyedTable
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class GenericDaoImpl[T <: KeyedEntity, TAB <: KeyedEntityTable[T]](tbl: Tag => TAB)
                                                                  (implicit db: Database,
                                                                   ec: ExecutionContext)
  extends TableQuery(tbl) with GenericDao[T] {

  override def findAll(): Future[Seq[T]] = db.run(this.result)

  override def findById(id: String): Future[Option[T]] = db.run(this.findBy(_.id).applied(id).result.headOption)

  override def insert(entity: T): Future[Unit] = db.run(this += entity) map dropValue

  override def insertAll(entities: Seq[T]): Future[Unit] = db.run(this ++= entities) map dropValue

  override def update(entity: T): Future[Unit] = db.run(this.findBy(_.id).applied(entity.id).update(entity)) map dropValue

  override def delete(id: String): Future[Boolean] = db.run(this.findBy(_.id).applied(id).delete) map (_ > 0)

  /**
    * Trivial function to drop a returned value and return Unit
    */
  protected val dropValue: Any => Unit = _ => ()

}

object GenericDaoImpl {

  type KeyedEntityTable[T] = Table[T] with KeyedTable

}