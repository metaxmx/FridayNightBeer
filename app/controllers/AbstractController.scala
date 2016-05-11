package controllers

import dto.FormErrorsDTO
import exceptions.ApiException
import play.api.data.Form
import play.api.http.ContentTypes.JSON
import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Results.Status
import play.api.mvc.{ActionBuilder, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds

@deprecated("building of new API", "2016-05-11")
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
      refine(request).flatMap(_.fold(Future.successful, block))

    protected def refine[A](request: Request[A]): Future[Either[Result, R[A]]]

  }

  object ApiAction extends ApiActionBuilder[Request] {

    override def invokeInner[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)

  }

  def validateApiForm[T](form: Form[T])(success: T => Future[Result])(implicit request: Request[JsValue]): Future[Result] =
    form.bindFromRequest.fold(form => {
      Future.successful(Status(BAD_REQUEST).apply(toJson(FormErrorsDTO.fromFormErrors(form.errors))).as(JSON))
    }, success)

}