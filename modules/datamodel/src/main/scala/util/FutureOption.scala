package util

import scala.concurrent._
import scala.concurrent.duration.Duration

/**
  * Monad combining a [[Future]] with an [[Option]]
  * Created by Christian on 05.05.2016.
  */
class FutureOption[+A](wrapped: Future[Option[A]]) extends Awaitable[Option[A]] {

  def toFuture: Future[Option[A]] = wrapped

  @throws[InterruptedException](classOf[InterruptedException])
  @throws[TimeoutException](classOf[TimeoutException])
  override def ready(atMost: Duration)(implicit permit: CanAwait): FutureOption.this.type = {
    wrapped.ready(atMost)
    this
  }

  @throws[Exception](classOf[Exception])
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

  def flatMap[S](f: A => Option[S])(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(
    wrapped map { _ flatMap f }
  )

  def flatMap[S](f: A => Future[Option[S]])(implicit executor: ExecutionContext): FutureOption[S] = new FutureOption(
    wrapped flatMap {
      case None => Future.successful(None)
      case Some(value) => f(value)
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

}

object FutureOption {

  def apply(): FutureOption[Nothing] = new FutureOption(Future.successful(None))

  def apply[A](future: Future[Option[A]]): FutureOption[A] = new FutureOption(future)

  def apply[A](option: Option[Future[A]])(implicit executor: ExecutionContext): FutureOption[A] = option match {
    case None => FutureOption()
    case Some(future) => new FutureOption(future map (Some(_)))
  }

  def apply[A](option: Option[A])(implicit executor: ExecutionContext): FutureOption[A] = option match {
    case None => FutureOption()
    case Some(value) => new FutureOption(Future.successful(option))
  }

}