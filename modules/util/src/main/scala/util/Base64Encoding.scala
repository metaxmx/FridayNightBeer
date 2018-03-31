package util

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

/**
  * Trait for Base64 Encoding and Decoding.
  * @author Christian Simon
  */
trait Base64Encoding {

  /** Base64 Encode: String -> String */
  def base64Encode(data: String): String = new String(base64EncodeBytes(data), UTF_8)

  /** Base64 Encode: Byte-Array -> String */
  def base64Encode(data: Array[Byte]): String = new String(base64EncodeBytes(data), UTF_8)

  /** Base64 Encode: String -> Byte-Array */
  def base64EncodeBytes(data: String): Array[Byte] = base64EncodeBytes(data.getBytes(UTF_8))

  /** Base64 Encode: Byte-Array -> Byte-Array */
  def base64EncodeBytes(data: Array[Byte]): Array[Byte] = Base64.getEncoder.encode(data)

  /** Base64 Decode: String -> String */
  def base64Decode(data: String): String = new String(base64DecodeBytes(data), UTF_8)

  /** Base64 Decode: Byte-Array -> String */
  def base64Decode(data: Array[Byte]): String = new String(base64DecodeBytes(data), UTF_8)

  /** Base64 Decode: String -> Byte-Array */
  def base64DecodeBytes(data: String): Array[Byte] = base64DecodeBytes(data.getBytes(UTF_8))

  /** Base64 Decode: Byte-Array -> Byte-Array */
  def base64DecodeBytes(data: Array[Byte]): Array[Byte] = Base64.getDecoder.decode(data)

}

object Base64Encoding extends Base64Encoding
