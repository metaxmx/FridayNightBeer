package storage

/**
  * Exception indicating a problem with the storage layer.
  * @param message exception message text
  * @param cause cause of the exception
  */
class StorageException(message: String, cause: Throwable = null) extends Exception(message, cause)
