package com.expedia.www.bex.datasci.sample.util

class ArgsParser(args: Array[String]) {
  var theDate: String = ""
  var daysForward: Integer = 0

  if (args != null && args.length > 2){
    theDate = args(2)
    if (args.length > 3){
      try {
        daysForward = args(3).toInt
      } catch {
        case e: Exception => None
      }
    }
  }
}
