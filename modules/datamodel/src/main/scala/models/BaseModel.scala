package models

case class BaseModel[T](collectionName: String)

trait BaseModelIdReader[T, K] {

  def getId: T => K

}

trait BaseModelIdWriter[T, K] {

  def withId: (T, K) => T

}