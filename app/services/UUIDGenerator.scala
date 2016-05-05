package services

import java.util.UUID
import javax.inject.Singleton

@Singleton
class UUIDGenerator {

  def generate: UUID = UUID.randomUUID()

  def generateStr: String = generate.toString

}