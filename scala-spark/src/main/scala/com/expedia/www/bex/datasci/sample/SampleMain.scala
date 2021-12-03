package com.expedia.www.bex.datasci.sample

import com.expedia.www.bex.datasci.sample.config.{AppOptions, AppOptionsUtil}
import com.expedia.www.bex.datasci.sample.daily.DailyPublisher
import com.expedia.www.bex.datasci.sample.monitor.{Logging, SplunkMetricsLogger}
import com.expedia.www.bex.datasci.sample.util.{ArgsParser, DateUtil}
import org.apache.spark.sql.SparkSession

object SampleMain extends Logging with SplunkMetricsLogger {
  var sparkSession:SparkSession = _

  def main(args: Array[String]) = {
    try {
      val options: AppOptions = AppOptionsUtil.loadOptions(args).get
      init(options)
      val s3Location: String = options.dataBucket + options.dataFolder
      val argsParser = new ArgsParser(args);
      val dateUtil = new DateUtil();
      val datesList: List[String] = dateUtil.getDates(argsParser.theDate, argsParser.daysForward)
      publishData(datesList, s3Location)
    } catch {
        case e:Exception =>
          logger.error(s"Error in main : ${e.getMessage}")
    } finally {
      if (sparkSession != null) {
        sparkSession.stop()
      }
    }
  }

  def init (options: AppOptions) : Unit = {
    sparkSession = SparkSessionBuilder.build(options)
    sparkSession.conf.set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    //GraphiteLogger.setOptions(options)
  }

  def publishData(theDates: List[String], s3location: String): Unit = {
    val dailyPublisher = new DailyPublisher();
    dailyPublisher.publish(sparkSession, theDates, s3location);
  }



}