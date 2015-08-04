package dto

import play.api.libs.json.Json
import play.api.data.FormError

case class ErrorDTO(error: String)

object ErrorDTO {

  implicit val jsonFormat = Json.format[ErrorDTO]

}

case class FormErrorMessageDTO(key: String, message: String, args: Option[Seq[String]])

object FormErrorMessageDTO {

  implicit val jsonFormat = Json.format[FormErrorMessageDTO]

}

case class FormErrorsDTO(errors: Seq[FormErrorMessageDTO])

object FormErrorsDTO {

  implicit val jsonFormat = Json.format[FormErrorsDTO]

  def fromFormErrors(errors: Seq[FormError]) =
    FormErrorsDTO(errors.flatMap {
      err =>
        err.messages.map {
          msg =>
            FormErrorMessageDTO(err.key, msg,
              if (err.args.isEmpty) None
              else Some(err.args.map(_.toString)))
        }
    })

}