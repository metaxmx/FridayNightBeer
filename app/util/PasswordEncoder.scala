package util

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest

object PasswordEncoder {

  def encodePassword(passwd: String) = hex(md5(passwd getBytes UTF_8))

  def md5(data: Array[Byte]) = MessageDigest getInstance "MD5" digest data

  def hex(data: Array[Byte]) = data.map { "%02X" format _ }.mkString.toLowerCase

}