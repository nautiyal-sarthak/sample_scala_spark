package com.expedia.www.bex.datasci.sample.monitor

trait SplunkMetricsLogger {

  import com.expedia.my.datasci.databricks.logger.SplunkMetricsLogger
  import SplunkMetricsLogger.defaultSplunkOptions

  def logSplunkMetrics(appName: String, eventName: String, metrics: Seq[Map[String, String]]) = {
    SplunkMetricsLogger.logMetricsWithAck(appName, eventName, metrics)
  }

  def logSplunkMetricsWithAck(appName: String, eventName: String, metrics: Seq[Map[String, String]], idField: String = "", waitStepSec: Int = 2, timeoutSec: Int = 2*60) = {
    SplunkMetricsLogger.logMetricsWithAck(appName, eventName, metrics, idField, waitStepSec, timeoutSec)
  }

}
