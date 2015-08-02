package models

import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter, BSONHandler, BSONDocument}
import play.api.libs.json.OFormat

case class BaseModel[T](collectionName: String)

trait BaseModelIdReader[T, K] {

  def getId: T => K

}

trait BaseModelIdWriter[T, K] {

  def withId: (T, K) => T

}

trait BaseModelSpec[T, K] {

  def baseModel: BaseModel[T]

  def baseModelIdReader: BaseModelIdReader[T, K]

  def baseModelIdWriter: BaseModelIdWriter[T, K]

  def bsonHandler: BSONDocumentReader[T] with BSONDocumentWriter[T] with BSONHandler[BSONDocument, T]

  def jsonFormat: OFormat[T]

}

class BaseModelImplicitSpec[T, K](implicit val baseModel: BaseModel[T],
                                  val baseModelIdReader: BaseModelIdReader[T, K],
                                  val baseModelIdWriter: BaseModelIdWriter[T, K],
                                  val bsonHandler: BSONDocumentReader[T] with BSONDocumentWriter[T] with BSONHandler[BSONDocument, T],
                                  val jsonFormat: OFormat[T]) extends BaseModelSpec[T, K]