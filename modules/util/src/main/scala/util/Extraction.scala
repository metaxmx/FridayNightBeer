package util

import scala.util.Try
import Base64Encoding.{base64Encode, base64DecodeBytes}

/**
  * Extractors.
  */
object Extraction {

  /**
    * Extractor for Integer encoded as String
    */
  object AsInt {

    def unapply(data: String): Option[Int] = Try(data.toInt).toOption

  }

  object Base64Blob {

    def apply(bytes: Array[Byte]): String = base64Encode(bytes)

    def unapply(base64Str: String): Option[Array[Byte]] = Try(base64DecodeBytes(base64Str)).toOption

  }

}
