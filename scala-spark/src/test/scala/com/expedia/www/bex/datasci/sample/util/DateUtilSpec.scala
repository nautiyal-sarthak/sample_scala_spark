package com.expedia.www.bex.datasci.sample.util

import java.time.{Instant, LocalDate}

import com.expedia.www.bex.datasci.sample.SparkJobSuiteRunner

class DateUtilSpec extends SparkJobSuiteRunner{

  val dateUtil = new DateUtil();
  val instantString: String = "1776-07-04T12:34:56.789Z"

  override def beforeAll(): Unit = {
    super.beforeAll()
    dateUtil.useFixedClock(Instant.parse(instantString))
  }

  test("Test getDates") {
    val datesList: List[String] = dateUtil.getDates("1776-07-01", 2)
    assert(datesList.length == 3)
  }

  test("Test getDates dates in past") {
    val datesList: List[String] = dateUtil.getDates("1776-06-01", 4)
    assert(datesList.length == 5)
  }

  test("Test getDates dates past today") {
    val datesList: List[String] = dateUtil.getDates("1776-07-01", 4)
    assert(datesList.length == 3)
  }

  test("Test getStartDate in past") {
    assert(dateUtil.getStartDate("1776-07-01").toString.equals("1776-07-01"))
  }

  test("Test getStartDate in future") {
    assert(dateUtil.getStartDate("1776-07-10").toString.equals("1776-07-10"))
  }

  test("Test getStartDate is null") {
    assert(dateUtil.getStartDate(null).toString.equals("1776-07-03"))
  }

  test("Test getStartDate is empty string") {
    assert(dateUtil.getStartDate("").toString.equals("1776-07-03"))
  }

  test("Test getStartDate is invalid") {
    assert(dateUtil.getStartDate("kjladssl").toString.equals("1776-07-03"))
  }

  test("Test getEndDate with 0 daysForward") {
    val startDate: LocalDate = LocalDate.parse("1776-07-01")
    assert(dateUtil.getEndDate(startDate, 0).toString.equals("1776-07-01"))
  }

  test("Test getEndDate with 1 daysForward") {
    val startDate: LocalDate = LocalDate.parse("1776-07-01")
    assert(dateUtil.getEndDate(startDate, 1).toString.equals("1776-07-02"))
  }

  test("Test getEndDate with daysForward past today") {
    val startDate: LocalDate = LocalDate.parse("1776-07-01")
    assert(dateUtil.getEndDate(startDate, 10).toString.equals("1776-07-03"))
  }

  test("Test getEndDate with daysForward as null") {
    val startDate: LocalDate = LocalDate.parse("1776-07-01")
    assert(dateUtil.getEndDate(startDate, null).toString.equals("1776-07-01"))
  }

  test("Test isNotEmpty") {
    assert(dateUtil.isNotEmpty("Not empty string") == true)
  }

  test("Test isNotEmpty null") {
    assert(dateUtil.isNotEmpty(null) == false)
  }

  test("Test isNotEmpty empty string") {
    assert(dateUtil.isNotEmpty("") == false)
  }

  test("Test isNotEmpty empty string with space") {
    assert(dateUtil.isNotEmpty("  ") == false)
  }

  test("Test getDQLookBackDate ") {
    val dqLookBackDate: LocalDate = dateUtil.getDQLookBackDate("1776-07-03", 2)
    assert(dqLookBackDate.toString.equals("1776-07-01"))
  }

  test("Test getDQLookBackDate invalid date") {
    val dqLookBackDate: LocalDate = dateUtil.getDQLookBackDate("invalid", 2)
    assert(dqLookBackDate.toString.equals("1776-07-04"))
  }

}
