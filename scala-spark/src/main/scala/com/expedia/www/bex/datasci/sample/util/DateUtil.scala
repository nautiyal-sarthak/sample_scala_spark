package com.expedia.www.bex.datasci.sample.util

import java.time.format.DateTimeFormatter
import java.time.{Clock, Instant, LocalDate, ZoneId}



class DateUtil {
  val datePattern: String = "yyyy-MM-dd"
  var clock: Clock = Clock.systemDefaultZone();

  def getDates(theDate: String, daysForward: Integer) : List[String] = {
    val startDate: LocalDate = getStartDate(theDate)
    val endDate: LocalDate = getEndDate(startDate, daysForward).plusDays(1)
    Iterator.iterate(startDate)(_ plusDays 1) takeWhile (_ isBefore endDate) map (_.toString) toList
  }

  def getStartDate(theDate: String): LocalDate = {
    var startDate: LocalDate = LocalDate.now(clock).minusDays(1)
    if (isNotEmpty(theDate)) {
      try {startDate = LocalDate.parse(theDate, DateTimeFormatter.ofPattern(datePattern))}
      catch {case e:Exception => None}
    }
    startDate
  }

  def getEndDate(startDate: LocalDate, daysForward: Integer): LocalDate = {
    var endDate: LocalDate = startDate
    if (daysForward != null && daysForward > 0) {
      val yesterday: LocalDate = LocalDate.now(clock).minusDays(1)
      endDate = startDate.plusDays(daysForward.toLong)
      if (endDate.isAfter(yesterday)) {
        endDate = yesterday
      }
    }
    endDate
  }

  def getDQLookBackDate(theDate: String, daysBack: Long): LocalDate = {
    var dqLookBackDate: LocalDate = {
      try {
        LocalDate.parse(theDate, DateTimeFormatter.ofPattern(datePattern)).minusDays(daysBack)
      }
      catch {
        case e: Exception => LocalDate.now(clock)
      }
    }
    dqLookBackDate
  }

  def isNotEmpty(x: String) = x != null && !x.trim.isEmpty

  def useFixedClock(instant: Instant): Unit = {
    clock = Clock.fixed(instant, ZoneId.of("UTC"))
  }

}
