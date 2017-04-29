package services

import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test UUIDGenerator
  */
class UUIDGeneratorTest extends WordSpec with MustMatchers {

  "A UUID Generator" should {
    "produce unique results" in {
      val gen = new UUIDGenerator
      val sampleSize = 1000
      val uuids = Stream.continually(gen.generateStr).take(sampleSize).toSet
      uuids.size mustBe sampleSize
    }
  }

  "Different UUID Generators" should {
    "produce defferent results" in {
      val gen = new UUIDGenerator
      val gen2 = new UUIDGenerator
      gen.generateStr mustNot be (gen2.generateStr)
    }
  }

}
