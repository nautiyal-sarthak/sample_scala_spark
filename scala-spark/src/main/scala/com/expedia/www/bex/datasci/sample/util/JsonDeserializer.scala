package com.expedia.www.bex.datasci.sample.util

import com.google.gson.Gson

trait JsonDeserializer {

  val jsonSerializer: Gson = new Gson

  def toJson[T](message: String, typeOfMessage: Class[T]): T = {
    jsonSerializer.fromJson(message, typeOfMessage)
  }

}