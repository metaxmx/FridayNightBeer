package util

import org.joda.time.LocalDateTime
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonAST.JString
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{CustomSerializer, DefaultFormats, Formats}

object JsonFormat extends JsonFormat

/**
  * Definition for json4s format
  */
trait JsonFormat {

  implicit val jsonFormat: Formats = DefaultFormats ++ JodaTimeSerializers.all + LocalDateTimeSerializer

}

/**
  * Serializer for LocalDateTime
  */
object LocalDateTimeSerializer extends CustomSerializer[LocalDateTime](format => (
  {
    case JString(s) => LocalDateTime.parse(s)
  },
  {
    case d: LocalDateTime => JString(ISODateTimeFormat.dateTimeNoMillis().print(d))
  }
))