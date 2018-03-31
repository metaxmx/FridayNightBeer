package util

import java.security.{MessageDigest, SecureRandom}
import java.nio.charset.StandardCharsets.UTF_8

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import util.Extraction._

/**
  * Secure Password hash generation and verification
  * (inspired by: https://gist.github.com/agemooij/9622302)
  */
class SecurePasswordHash {

  private val random = new SecureRandom()

  private val hashSaltSeparator = ":"

  private val defaultHashIterations = 2000

  private val bytesOfSalt = 16
  private val bytesOfPasswordHash = 32

  val UNSECURE_MD5 = "UnsecureMD5"

  /**
    * Secure hash password with random salt (default hash iterations).
    * @param password password input
    * @return password spec in format "$iterations:$salt:$hash"
    */
  def hashPassword(password: String): String = hashPassword(password, generateRandomBytes(bytesOfSalt))

  /**
    * Secure hash password (default hash iterations).
    * @param password password input
    * @param salt random salt for password
    * @return password spec in format "$iterations:$salt:$hash"
    */
  def hashPassword(password: String, salt: Array[Byte]): String = hashPassword(password, salt, defaultHashIterations)

  /**
    * Secure hash password.
    * @param password password input
    * @param salt random salt for password
    * @param nrOfIterations number of PBKDF2 iterations
    * @return password spec in format "$iterations:$salt:$hash"
    */
  def hashPassword(password: String, salt: Array[Byte], nrOfIterations: Int): String = {
    val hash = pbkdf2(password, salt, nrOfIterations)
    (s"$nrOfIterations" :: Base64Blob(salt) :: Base64Blob(hash) :: Nil).mkString(hashSaltSeparator)
  }

  /**
    * Validate password against a password spec.
    * @param password the password to validate
    * @param hashedPassword the hashed password (either in format "$iterations:$salt:$hash",
    *                       or for legacy unsecure MD5 hashes as "UnsecureMD5:$hash"
    * @return true if the password is valid
    */
  def validatePassword(password: String, hashedPassword: String): Boolean = {
    hashedPassword.split(hashSaltSeparator).toList match {
      case AsInt(nrOfIterations) :: Base64Blob(salt) :: storedHash64 :: Nil =>
        val calculatedHash = pbkdf2(password, salt, nrOfIterations)
        Base64Blob(calculatedHash) == storedHash64
      case UNSECURE_MD5 :: md5hash :: Nil =>
        val calculatedHash = hex(MessageDigest.getInstance("MD5").digest(password.getBytes(UTF_8)))
        calculatedHash == md5hash
      case _ =>
        false
    }
  }

  /**
    * Password-based Key Derivative Function.
    * @param password password to encrypt
    * @param salt random salt
    * @param nrOfIterations number of hash iterations
    * @return encrypted password hash
    */
  private def pbkdf2(password: String, salt: Array[Byte], nrOfIterations: Int): Array[Byte] = {
    val keySpec = new PBEKeySpec(password.toCharArray, salt, nrOfIterations, bytesOfPasswordHash * 8)
    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(keySpec).getEncoded
  }

  /**
    * Encode byte array as HEX.
    * @param data byte data
    * @return hex string
    */
  private def hex(data: Array[Byte]) = data.map { "%02X" format _ }.mkString.toLowerCase

  /**
    * Generate random bytes from SecureRandom.
    * @param length length of result array
    * @return random bytes
    */
  private def generateRandomBytes(length: Int): Array[Byte] = {
    val keyData = new Array[Byte](length)
    random.nextBytes(keyData)
    keyData
  }

}

object SecurePasswordHash extends SecurePasswordHash