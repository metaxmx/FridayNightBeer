package storage.mongo

import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}


trait BSONContext[T] {

  def bsonWriter: BSONDocumentWriter[T]

  def bsonReader: BSONDocumentReader[T]

}
