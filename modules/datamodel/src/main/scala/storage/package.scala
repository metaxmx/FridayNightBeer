import models.User

/**
  * Package object for storage package.
  */
package object storage {

  class StorageException(message: String, cause: Throwable = null) extends Exception(message, cause)

}
