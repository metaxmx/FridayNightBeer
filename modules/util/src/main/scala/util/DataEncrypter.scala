package util

/**
  * Utility to encrypt and decrypt data.
  */
trait DataEncrypter {

  /**
    * Encrypt byte data.
    * @param data input data
    * @return encrypted output data
    */
  def encryptBytes(data: Array[Byte]): Array[Byte]

  /**
    * Encrypt string data.
    * @param data input String
    * @return encrypted output String
    */
  def encryptString(data: String): String

  /**
    * Decrypt byte data.
    * @param encryptedData encrypted input data
    * @return decrypted output data
    */
  def decryptBytes(encryptedData: Array[Byte]): Array[Byte]

  /**
    * Decrypt string data.
    * @param encryptedString encrypted input String
    * @return decrypted output String
    */
  def decryptString(encryptedString: String): String

}
