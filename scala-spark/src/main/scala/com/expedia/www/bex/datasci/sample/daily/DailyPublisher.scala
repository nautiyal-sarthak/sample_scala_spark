package com.expedia.www.bex.datasci.sample.daily

import com.expedia.www.bex.datasci.sample.monitor.Logging
import org.apache.spark.sql.SparkSession

class DailyPublisher() extends Logging {

  def publish(sparkSession: SparkSession, theDates: List[String], s3location: String) : Unit = {
    for( theDate <- theDates) {
      logInfo("Started keyword data for " + theDate)
      val keywordDaily = new KeywordDaily(sparkSession, s3location, theDate)
      keywordDaily.load()
                  .repartition(16)
                  .write.mode("overwrite")
                  .partitionBy("the_date", "search_engine", "brand", "lob", "keyword_type")
                  .parquet(s3location + "keyword")
      logInfo("Completed keyword data for " + theDate)
    }

  }

}