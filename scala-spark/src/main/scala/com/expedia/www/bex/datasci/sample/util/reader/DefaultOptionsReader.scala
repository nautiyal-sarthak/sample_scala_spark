package com.expedia.www.bex.datasci.sample.util.reader

import com.expedia.www.bex.datasci.sample.util.options.SparkOptions

trait DefaultOptionsReader[T <: SparkOptions] extends OptionsReader[T] {

  override def readOptions(args: Array[String]): T = {
    if (args.length == 0) {
      super.readOptions(Array("-r", "sandbox.json"))
    } else {
      super.readOptions(args)
    }
  }
}
