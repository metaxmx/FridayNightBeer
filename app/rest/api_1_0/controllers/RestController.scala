package rest.api_1_0.controllers

import play.api.mvc.{ActionBuilder, Request, Result}
import rest.Exceptions.RestException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Created by Christian Simon on 30.04.2016.
  */
trait RestController {

  trait RestActionBuilder[+R[_]] extends ActionBuilder[R] {

    final def invokeBlock[A](request: Request[A], block: (R[A]) => Future[Result]) = {
      implicit val req = request
      invokeInner(request, block) recover {
        case RestException(re) => re.toResult
      }
    }

    def invokeInner[A](request: Request[A], block: (R[A]) => Future[Result]): Future[Result]

  }

  trait RestActionRefiner[+R[_]] extends RestActionBuilder[R] {

    final override def invokeInner[A](request: Request[A], block: R[A] => Future[Result]) =
      refine(request).flatMap(_.fold(Future.successful, block))

    protected def refine[A](request: Request[A]): Future[Either[Result, R[A]]]

  }

  object RestAction extends RestActionBuilder[Request] {

    override def invokeInner[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)

  }

}
