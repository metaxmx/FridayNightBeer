package storage.mongo

import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}


trait BSONContext[T] {

  implicit val bsonWriter: BSONDocumentWriter[T]

  implicit val bsonReader: BSONDocumentReader[T]

}
