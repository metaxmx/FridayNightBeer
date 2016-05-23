package util

import org.json4s.DefaultFormats
import org.json4s.ext.JodaTimeSerializers

object JsonFormat extends JsonFormat

/**
  * Definition for json4s format
  */
trait JsonFormat {

  implicit val jsonFormat = DefaultFormats ++ JodaTimeSerializers.all

}
