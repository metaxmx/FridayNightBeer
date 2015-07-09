package models

trait BaseModel {

  def collectionName: String

}

trait BaseModelIdReader[T] {

  def getId: T => Int

}

trait BaseModelIdWriter[T] {

  def withId: (T, Int) => T

}