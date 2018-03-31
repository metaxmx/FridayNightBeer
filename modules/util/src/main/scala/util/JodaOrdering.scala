package util

import org.joda.time.DateTime

object JodaOrdering {

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

}
