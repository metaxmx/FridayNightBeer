package util

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Monad combining a [[scala.concurrent.Future]] with an [[scala.Option]]
  * @param wrapped wrapped future
  * @tparam A value type
  */
class FutureOption[+A](wrapped: Future[Option[A]]) extends Awaitable[Option[A]] {

  /**
    * Return wrapped future containing the option.
    * @return future of option
    */
  def toFuture: Future[Option[A]] = wrapped

  @throws[InterruptedException]
  @throws[TimeoutException]
  override def ready(atMost: Duration)(implicit permit: CanAwait): FutureOption.this.type = {
    wrapped.ready(atMost)
    this
  }

  @throws[Exception]
  override def result(atMost: Duration)(implicit permit: CanAwait): Option[A] = wrapped.result(atMost)

  /* Monadic operations */

  /**
    * Execute function once after the future resolved successfully, and only if the option contains a value
    * @param f procedure to execute for the value
    * @param executor implicit execution context
    * @tparam U procedure result type
    */
  def foreach[U](f: A => U)(implicit executor: ExecutionContext): Unit = wrapped.foreach { _ foreach f  }

  /**
    * Map value of successful future containing non-empty option to new value by given function.
    * If the future fails or the option is empty, the result is returned unchanged.
    * The original future-option is not changed.
    * @param f function to map the value with
    * @param executor implicit execution context
    * @tparam S function result type
    * @return new future-option containing the modified future
    */
  def map[S](f: A => S)(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(wrapped.map { _ map f })

  /**
    * Map value of successful future containing non-empty option to new future-option by given function.
    * If the future fails or the option is empty, the result is returned unchanged.
    * The original future-option is not changed.
    * @param f function to map the value to new future-option
    * @param executor implicit execution context
    * @tparam S value type of resulting future-option
    * @return new future-option containing the modified future
    */
  def flatMap[S](f: A => FutureOption[S])(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(
    wrapped flatMap {
      case None => Future.successful(None)
      case Some(value) => f(value).toFuture
    }
  )

  /**
    * Filter value of successful future containing non-empty option by applying a predicate.
    * If the future fails or the option is empty, the result is returned unchanged. If the predicate
    * returns false for a value, the returned future-option will contain an empty future.
    * The original future-option is not changed.
    * @param p predicate for the filter
    * @param executor implicit execution context
    * @return new future-option containing the modified future
    */
  def filter(p: A => Boolean)(implicit executor: ExecutionContext): FutureOption[A] = new FutureOption(
    wrapped map { _ filter p }
  )

  /**
    * @see filter
    */
  def withFilter(p: A => Boolean)(implicit executor: ExecutionContext): FutureOption[A] = filter(p)(executor)

  /**
    * Provide another future-option to resolve to, in case the future resolved successfully to an empty option.
    * If the future fails, the result is returned unmodified.
    * The original future-option is not changed.
    * @param f by-name future-option expression to use in case the result option is empty
    * @param executor implicit execution context
    * @tparam B value type of the other future-option
    * @return new future-option containing the modified future
    */
  def orElse[B >: A](f: => FutureOption[B])(implicit executor: ExecutionContext): FutureOption[B] = new FutureOption(
    wrapped flatMap {
      case None => f.toFuture
      case Some(_) => wrapped
    }
  )

  /**
    * Apply side-effect after execution of the wrapped future, and return new future-option with the
    * value of the original future-option which will resolve after the side-effect was applied
    * The original future-option is not changed.
    * @param pf partial function to apply as side-effect
    * @param executor implicit execution context
    * @tparam B result type of the partial function
    * @return new future-option containing the modified future
    */
  def andThen[B](pf: PartialFunction[Try[Option[A]], B])(implicit executor: ExecutionContext): FutureOption[A] =
    new FutureOption(wrapped.andThen(pf))

  /**
    * Provide a default value to resolve to, in case the future resolved successfully to an empty option.
    * @param onEmpty default value to use in case the original value-option is empty
    * @param executor implicit execution context
    * @tparam B  value type of the default value
    * @return future of either the contained value or the default value
    */
  def flatten[B >: A](onEmpty: => B)(implicit executor: ExecutionContext): Future[B] = wrapped map { _ getOrElse onEmpty }

  /**
    * Resolve the option of a successful future evaluation by providing a default future to return in case the option is empty,
    * and a mapping future from the value, in case the option is non-empty.
    * If the future fails, the result is returned unmodified.
    * The original future-option is not changed.
    * @param onEmpty future to return if the original future resolves to an empty option
    * @param f mapping future to apply if the original future resolves to a non-empty option
    * @param executor implicit execution context
    * @tparam B resulting future value type
    * @return future of the mapped evaluation
    */
  def fold[B](onEmpty: => Future[B])(f: A => Future[B])(implicit executor: ExecutionContext): Future[B] =
    wrapped flatMap { _.fold(onEmpty)(f) }

}

object FutureOption {

  /**
    * Create empty future-option (immediately successful, containing no value)
    */
  def apply(): FutureOption[Nothing] = new FutureOption(Future.successful(None))

  /**
    * Create future-option from future of option.
    */
  def apply[A](future: Future[Option[A]]): FutureOption[A] = new FutureOption(future)

  /**
    * Create future-option from option of future.
    */
  def apply[A](option: Option[Future[A]])(implicit executor: ExecutionContext): FutureOption[A] = option match {
    case None => FutureOption()
    case Some(future) => FutureOption.fromFuture(future)
  }

  /**
    * Create future-option from option (immediately successful, containing the given option).
    */
  def fromOption[A](option: Option[A])(implicit executor: ExecutionContext): FutureOption[A] =
    new FutureOption(Future.successful(option))

  /**
    * Create future-option from future (containing the future value as Some after successful resolution).
    */
  def fromFuture[A](future: Future[A])(implicit executor: ExecutionContext): FutureOption[A] =
    new FutureOption(future map Some.apply)

}
