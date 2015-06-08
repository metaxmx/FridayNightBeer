package exceptions

case class QueryException(message: String, cause: Throwable) extends Exception(message, cause) {

  def this(cause: Throwable) = this(null, cause)

  def this(message: String) = this(message, null)

  def this() = this(null, null)

}

object QueryException {

  def appy(cause: Throwable) = new QueryException(cause)

  def apply(message: String) = new QueryException(message)

  def apply() = new QueryException

}