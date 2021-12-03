package com.expedia.www.bex.datasci.sample.util

import com.expedia.www.bex.datasci.sample.SparkJobSuiteRunner

class ArgsParserSpec extends SparkJobSuiteRunner {

  test("Test ArgsParser with null args") {
    val argsParser = new ArgsParser(null);
    assert(argsParser.theDate.equals(""))
    assert(argsParser.daysForward == 0)
  }

  test("Test ArgsParser with empty args") {
    val argsParser = new ArgsParser(Array());
    assert(argsParser.theDate.equals(""))
    assert(argsParser.daysForward == 0)
  }

  test("Test ArgsParser with 2 element args") {
    val argsParser = new ArgsParser(Array("1", "2"));
    assert(argsParser.theDate.equals(""))
    assert(argsParser.daysForward == 0)
  }

  test("Test ArgsParser with 3 element args") {
    val argsParser = new ArgsParser(Array("1", "2", "3"));
    assert(argsParser.theDate.equals("3"))
    assert(argsParser.daysForward == 0)
  }

  test("Test ArgsParser with 4 element args - good") {
    val argsParser = new ArgsParser(Array("1", "2", "3", "4"));
    assert(argsParser.theDate.equals("3"))
    assert(argsParser.daysForward == 4)
  }

  test("Test ArgsParser with 4 element args - bad") {
    val argsParser = new ArgsParser(Array("1", "2", "3", "ddd"));
    assert(argsParser.theDate.equals("3"))
    assert(argsParser.daysForward == 0)
  }
  
}
