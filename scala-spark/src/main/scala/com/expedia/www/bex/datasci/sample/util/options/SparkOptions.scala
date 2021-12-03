package com.expedia.www.bex.datasci.sample.util.options

trait SparkOptions {
  val numReaderThreads: Int = 1
  val numExecutors: Int = 1
  val checkPointInS3: Boolean = false
  val checkPointEnabled: Boolean = true
  val checkPointFolder: String = ""
  val jobName: String = null
  val localMode: Boolean = false
}