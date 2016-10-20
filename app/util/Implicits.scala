package util

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import org.json4s.native.JsonMethods._
import org.json4s.{JValue, MappingException}
import play.api.http.ContentTypes.JSON
import play.api.http.{ContentTypes, Writeable}
import play.api.libs.streams.Accumulator
import play.api.mvc.{BodyParser, Codec}
import util.Exceptions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.higherKinds
import scala.util.{Failure, Success, Try}

/**
  * Implicit values for this package.
  * Created by Christian Simon on 30.04.2016.
  */
object Implicits {

  implicit val formats = JsonFormat.jsonFormat

  implicit val codec = Codec.utf_8

  val JSON_UTF8 = ContentTypes.withCharset(JSON)

  implicit val jsonWritable = new Writeable[JValue](codec.encode compose pretty compose render, Some(JSON_UTF8))

  def jsonREST: BodyParser[JValue] = jsonREST[JValue]

  def jsonREST[A : Manifest]: BodyParser[A] = BodyParser("jsonREST") {
    implicit header =>
      def withError(exc: => RestException) = Left(exc.toResult)
      header.contentType.map(_.toLowerCase()) match {
        case None | Some(JSON) | Some("text/json") =>
          Accumulator(
            Sink.fold[ByteString, ByteString](ByteString.empty)((state, bs) => state ++ bs)
          ) map {
            bytes =>
              parseOpt(codec.decode(bytes)) match {
                case Some(json) =>
                  Try(json.extract[A]) match {
                    case Success(extracted) => Right(extracted)
                    case Failure(e: MappingException) => withError(JsonExtractException(e))
                    case Failure(e) => withError(BadRequestException(e))
                  }
                case None =>
                  withError(JsonParseException())
              }
          }
        case _ =>
          Accumulator.done(withError(UnsupportedMediaTypeException(JSON)))
      }
  }

}
