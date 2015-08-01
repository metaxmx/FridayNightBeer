package services

import javax.inject.Singleton
import java.util.UUID
import com.google.inject.ImplementedBy

/**
 * A type declaring the interface that will be injectable.
 */
@ImplementedBy(classOf[SimpleUUIDGenerator])
abstract class UUIDGenerator() {
  def generate: UUID
}

/**
 * A simple implementation of UUIDGenerator that we will inject.
 */
@Singleton
class SimpleUUIDGenerator extends UUIDGenerator {
  def generate: UUID = UUID.randomUUID()
}