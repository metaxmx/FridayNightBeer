package util

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import reactivemongo.bson._

object Joda {

  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  implicit def bsonHandler = BSONDateTimeHandler
}

object BSONDateTimeHandler extends BSONHandler[BSONDateTime, DateTime] {

  def read(time: BSONDateTime) = new DateTime(time.value, DateTimeZone.UTC)

  def write(jdtime: DateTime) = BSONDateTime(jdtime.getMillis)

}
