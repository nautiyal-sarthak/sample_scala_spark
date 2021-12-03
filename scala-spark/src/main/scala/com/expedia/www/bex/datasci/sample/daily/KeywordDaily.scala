package com.expedia.www.bex.datasci.sample.daily

import com.expedia.www.bex.datasci.sample.monitor.Logging
import com.expedia.www.bex.datasci.sample.util.{DataQualityUtil, DateUtil}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

class KeywordDaily(spark: SparkSession, s3Location: String, theDate: String) extends Logging{

  val daysBackForDQCheck: Long = 8
  var dataAvailable: Boolean = isDataAvailable()

  def isDataAvailable() : Boolean = {
    val dataAvailabilityQuery = "SELECT report_date FROM bexg_prod_sync.lz_sem_str_keywords " +
                                "WHERE report_date = \'{theDate}\' LIMIT 1"
    val df = spark.sql(dataAvailabilityQuery.replace("{theDate}", theDate))
    df.count() > 0
  }

  def load() : DataFrame = {
    var keywordDF: DataFrame = spark.emptyDataFrame;
    if (dataAvailable) {
      val query: String = "SELECT search_engine, brand, " +
          "CASE SPLIT(account_name,\':\')[3] " +
            "WHEN \'$\' THEN \'Hotel\' " +
            "WHEN \'%\' THEN \'Package\' " +
            "WHEN \'@\' THEN \'Flight\' " +
            "WHEN \'^\' THEN \'Car\' " +
            "WHEN \']\' THEN \'LX\' " +
            "WHEN \'#\' THEN \'Rail\' " +
            "WHEN \'~\' THEN \'Cruise\' " +
            "ELSE \'Unknown\' END AS lob, " +
          "SPLIT(ad_group_name,\':\')[3] AS keyword_type, keyword_text, keyword_id, " +
          "expedia_keyword_id , ad_group_id, campaign_id, account_id, match_type, ad_group_name, " +
          "campaign_name, account_name, geo_id, quality_score, post_click_quality_score, " +
          "creative_quality_score, cpc_bid, first_position_cpc, top_of_page_cpc, first_page_cpc, " +
          "system_serving_status " +
        "FROM bexg_prod_sync.lz_sem_str_keywords " +
        "WHERE report_date = \'{theDate}\' " +
          "AND keyword_status IN (\'enabled\', \'Active\') " +
          "AND campaign_status IN (\'enabled\', \'Active\')" +
          "AND ad_group_status IN (\'enabled\', \'Active\') " +
          "AND UPPER(account_name) NOT LIKE \'EXPEDIA%\' " +
          "AND CARDINALITY(SPLIT(account_name, ':')) >= 5 " +
          "AND CARDINALITY(SPLIT(campaign_name, ':')) >= 4 " +
          "AND CARDINALITY(SPLIT(ad_group_name, ':')) >= 4 " +
          "AND SPLIT(account_name,\':\')[3] IN (\'$\', \'%\', \'@\', \'^\', \']\', \'#\', \'~\') " +
          "AND SPLIT(ad_group_name,\':\')[3] IN (\'AC\', \'AT\', \'BT\', \'DL\', \'DT\', \'GR\', " +
                                                  "\'GT\', \'LM\', \'OD\', \'OG\', \'PT\') "

      keywordDF = spark.sql(query.replace("{theDate}", theDate))
                        .withColumn("the_date", lit(theDate))
      doDQCheck(currentDF = keywordDF)
    } else {
      logger.error("Unavailable data: bexg_prod_sync.lz_sem_str_keywords - " + theDate)
    }
    keywordDF
  }

  def doDQCheck(currentDF: DataFrame): Unit = {
    var historicalDF : DataFrame = spark.emptyDataFrame
    val dateUtil: DateUtil = new DateUtil()
    val lookBackDate: String = dateUtil.getDQLookBackDate(theDate, daysBackForDQCheck).toString
    try {
      logger.info("Started DQ check")
      historicalDF = spark.read.parquet(s3Location + "keyword/")
                                .filter(col("the_date") >= lookBackDate
                                  && col("the_date") < theDate)
      val dataQualityUtil: DataQualityUtil = new DataQualityUtil()
      val columnNames = List("quality_score", "cpc_bid", "first_position_cpc", "top_of_page_cpc", "first_page_cpc")
      val summaryDF = dataQualityUtil.doDQCheck(currentDF, historicalDF, columnNames)
      val arrRows = summaryDF.collect()
      arrRows.map(row => logger.info(row.mkString(",")))
      logger.info("Completed DQ check")
    } catch {
      case e:Exception =>
        logger.error(s"Error reading historical parquet data : ${e.getMessage}")
    }
  }



}