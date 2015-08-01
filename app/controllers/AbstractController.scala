package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

import play.api.mvc.{ ActionBuilder, Request, Result }

import exceptions.ApiException

trait AbstractController {

  trait ApiActionBuilder[+R[_]] extends ActionBuilder[R] {

    final def invokeBlock[A](request: Request[A], block: (R[A]) => Future[Result]) = {
      invokeInner(request, block) recover {
        case e: ApiException => e.toResult
      }
    }

    def invokeInner[A](request: Request[A], block: (R[A]) => Future[Result]): Future[Result]

  }

  trait ApiActionRefiner[+R[_]] extends ApiActionBuilder[R] {

    final override def invokeInner[A](request: Request[A], block: R[A] => Future[Result]) =
      refine(request).flatMap(_.fold(Future.successful _, block))

    protected def refine[A](request: Request[A]): Future[Either[Result, R[A]]]

  }

  object ApiAction extends ApiActionBuilder[Request] {

    override def invokeInner[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)

  }

}