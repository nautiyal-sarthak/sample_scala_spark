package com.expedia.www.bex.datasci.sample.util.reader

import com.expedia.www.bex.datasci.sample.util.options.SparkOptions
import com.google.gson.Gson

trait ClassPathOptionsReader[T <: SparkOptions] extends OptionsReader[T] {

  private val gson = new Gson

  override def readOptions(args: Array[String]): T = {
    if ((args.length > 1) && args(0).equalsIgnoreCase("-r")) {
      var json = ""
      if (args.length > 3 && args(2).equalsIgnoreCase("-n")) {
        json = SparkJobConfigurationLoader.loadConfigurations(args(1), args(3))
      } else {
        json = SparkJobConfigurationLoader.loadConfigurations(args(1))
      }
      gson.fromJson(json, optionsType)
    } else {
      super.readOptions(args)
    }
  }
}
