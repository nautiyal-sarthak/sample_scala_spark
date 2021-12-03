package com.expedia.www.bex.datasci.sample

import com.expedia.www.bex.datasci.sample.config.AppOptions
import com.expedia.www.bex.datasci.sample.monitor.Logging
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkSessionBuilder extends Logging {

  def build(options: AppOptions): SparkSession = {
    val sparkConf: SparkConf = getSparkConf(options)
    implicit val  ss = SparkSession.builder
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
    ss
  }

  private def getSparkConf(options: AppOptions): SparkConf = {
    val sparkConf: SparkConf = new SparkConf().setAppName(options.appName)
    if (options.localMode)
      sparkConf.setMaster("local[*]") // use localMode, run "sudo hostname -s 127.0.0.1" first
    sparkConf
  }
}