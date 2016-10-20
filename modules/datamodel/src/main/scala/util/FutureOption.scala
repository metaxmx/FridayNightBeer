package util

import scala.concurrent._
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Monad combining a [[scala.concurrent.Future]] with an [[scala.Option]]
  * Created by Christian on 05.05.2016.
  */
class FutureOption[+A](wrapped: Future[Option[A]]) extends Awaitable[Option[A]] {

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

  def foreach[U](f: A => U)(implicit executor: ExecutionContext): Unit = wrapped.foreach { _ foreach f  }

  def map[S](f: A => S)(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(wrapped.map { _ map f })

  def flatMap[S](f: A => FutureOption[S])(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(
    wrapped flatMap {
      case None => Future.successful(None)
      case Some(value) => f(value).toFuture
    }
  )

  def filter(p: A => Boolean)(implicit executor: ExecutionContext): FutureOption[A] = new FutureOption(
    wrapped map { _ filter p }
  )

  def withFilter(p: A => Boolean)(implicit executor: ExecutionContext): FutureOption[A] = filter(p)(executor)

  def orElse[B >: A](f: => FutureOption[B])(implicit executor: ExecutionContext): FutureOption[B] = new FutureOption(
    wrapped flatMap {
      case None => f.toFuture
      case Some(value) => wrapped
    }
  )

  def andThen[B](pf: PartialFunction[Try[Option[A]], B])(implicit executor: ExecutionContext): FutureOption[A]  =
    new FutureOption(wrapped.andThen(pf))

  def flatten[B >: A](onEmpty: => B)(implicit executor: ExecutionContext): Future[B] = wrapped map { _ getOrElse onEmpty }

  def fold[B](onEmpty: => Future[B])(f: A => Future[B])(implicit executor: ExecutionContext): Future[B] =
    wrapped flatMap { _.fold(onEmpty)(f) }

}

object FutureOption {

  def apply(): FutureOption[Nothing] = new FutureOption(Future.successful(None))

  def apply[A](future: Future[Option[A]]): FutureOption[A] = new FutureOption(future)

  def apply[A](option: Option[Future[A]])(implicit executor: ExecutionContext): FutureOption[A] = option match {
    case None => FutureOption()
    case Some(future) => new FutureOption(future map (Some(_)))
  }

  def fromOption[A](option: Option[A])(implicit executor: ExecutionContext): FutureOption[A] =
    new FutureOption(Future.successful(option))

}
