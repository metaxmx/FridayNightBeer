package util

import org.joda.time.DateTime
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Tests for Joda Ordering
  */
class JodaTest extends WordSpec with MustMatchers {

  "Joda Ordering" should {
    "sort date times" in {

      val early = DateTime.parse("2015-05-22T14:33")
      val later = DateTime.parse("2017-10-03T09:02")

      import Joda.dateTimeOrdering

      val dates = Seq(later, early)
      dates.sorted mustBe Seq(early, later)

    }
  }

}
