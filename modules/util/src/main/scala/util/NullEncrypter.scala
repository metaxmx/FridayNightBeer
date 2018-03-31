package util

/**
  * Encrypter which does not perform any encryption (for development/test purpose).
  */
class NullEncrypter extends DataEncrypter {

  override def encryptBytes(data: Array[Byte]): Array[Byte] = data

  override def encryptString(data: String): String = data

  override def decryptBytes(encryptedData: Array[Byte]): Array[Byte] = encryptedData

  override def decryptString(encryptedString: String): String = encryptedString
}
