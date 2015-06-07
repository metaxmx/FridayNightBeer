package services

import java.security.MessageDigest
import org.apache.commons.codec.Charsets.UTF_8
import com.google.common.io.BaseEncoding

object PasswordEncoder {

  def encodePassword(passwd: String) = base64(md5(passwd.getBytes(UTF_8)))

  def md5(data: Array[Byte]) = MessageDigest.getInstance("MD5").digest(data)

  def base64(data: Array[Byte]) = BaseEncoding.base64().encode(data)

}