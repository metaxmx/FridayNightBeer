package util

import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import java.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
  * Encrypter with AES.
  * @param key private key
  */
class AESEncrypter(key: String) extends DataEncrypter {

  import AESEncrypter._

  private[this] lazy val keySpec: SecretKeySpec = {
    var keyBytes: Array[Byte] = (SALT + key).getBytes(UTF_8)
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
  }

  private[this] def createCipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

  override def encryptBytes(data: Array[Byte]): Array[Byte] = {
    val cipher = createCipher
    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
    cipher.doFinal(data)
  }

  override def encryptString(data: String) = new String(encryptBytes(data.getBytes(UTF_8)), UTF_8)

  override def decryptBytes(encryptedData: Array[Byte]): Array[Byte] = {
    val cipher = createCipher
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    cipher.doFinal(encryptedData)
  }

  override def decryptString(encryptedString: String): String  = new String(decryptBytes(encryptedString.getBytes(UTF_8)), UTF_8)

}

object AESEncrypter {

  private val SALT: String = "dY>oeoXn3QM*nvflAcHMIc"

}