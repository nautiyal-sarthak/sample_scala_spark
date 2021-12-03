package com.expedia.www.bex.datasci.sample.util.reader

import com.expedia.www.bex.datasci.sample.util.options.SparkOptions
import com.google.gson.GsonBuilder

trait CommandLineOptionsReader[T <: SparkOptions] extends OptionsReader[T] {

  private val gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create

  override def readOptions(args: Array[String]): T = {
    if ((args.length > 1) && args(0).equalsIgnoreCase("-j")) {
      gson.fromJson(args(1), optionsType)
    } else {
      super.readOptions(args)
    }
  }
}
