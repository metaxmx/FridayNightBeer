package models

/**
  * Base class for all model persisted in the database.
  * @tparam T type of base model itself
  */
abstract class BaseModel[T <: BaseModel[T]] {
  self: T =>

  /**
    * Get storage ID.
    * @return ID as String
    */
  val _id: String

  /**
    * Set storage ID.
    * @param id new id
    * @return copy of model with given id
    */
  def withId(id: String): T

}