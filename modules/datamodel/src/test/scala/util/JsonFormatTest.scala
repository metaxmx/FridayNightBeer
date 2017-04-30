package util

import org.joda.time.LocalDateTime
import org.json4s.MappingException
import org.json4s.native.Serialization.{read, write}
import org.scalatest.{MustMatchers, WordSpec}
import util.JsonFormat._
import util.JsonFormatTest.MyData

/**
  * Test for Json format
  */
class JsonFormatTest extends WordSpec with MustMatchers {

  "A JSON with Joda Time Format" should {

    "be encoded to JSON" in {
      val data = MyData("myId", LocalDateTime.parse("2007-12-03T10:15:30"))
      val json = write(data)
      val expectedJSON = """{"id":"myId","date":"2007-12-03T10:15:30"}"""
      json mustBe expectedJSON
    }

    "be decoded from JSON with Millis" in {
      val json = """{"id":"myId","date":"2015-08-02T03:27:28.123"}"""
      val myData = read[MyData](json)
      myData.id mustBe "myId"
      myData.date mustBe LocalDateTime.parse("2015-08-02T03:27:28.123")
    }

    "be decoded from JSON without Millis" in {
      val json = """{"id":"myId","date":"2015-08-02T03:27:54"}"""
      val myData = read[MyData](json)
      myData.id mustBe "myId"
      myData.date mustBe LocalDateTime.parse("2015-08-02T03:27:54")
    }

    "throw an exception if decoded from wrong format" in {
      val json = """{"id":"myId","date":"32.05.2017"}"""
      intercept[MappingException](read[MyData](json))
    }

    "throw an exception if decoded with invalid date" in {
      val json = """{"id":"myId","date":"2015-08-99T03:88:54"}"""
      intercept[MappingException](read[MyData](json))
    }

  }

}

object JsonFormatTest {

  case class MyData(id: String, date: LocalDateTime)

}
