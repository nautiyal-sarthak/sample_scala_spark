package com.expedia.www.bex.datasci.sample.daily

import com.expedia.www.bex.datasci.sample.SparkJobSuiteRunner
//import com.expedia.www.bex.datasci.sample.daily.KeywordDaily
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

class KeywordDailySpec extends SparkJobSuiteRunner {

  val s3Location = "src/test/resources/data/"

  override def beforeAll(): Unit = {
    super.beforeAll()
    val source: String = "csv"
    val dataPath: String = "src/test/resources/data/"
    spark.sql("CREATE DATABASE IF NOT EXISTS bexg_prod_sync LOCATION \'" + dataPath + "\'")

    val keywordTableName: String = "bexg_prod_sync.lz_sem_str_keywords"
    val keywordDataPath: String = dataPath + "lz_sem_str_keywords/"
    spark.catalog.createTable(keywordTableName, source, Map("path" -> keywordDataPath, "header" -> "true"))
  }

  test("Test isDataAvailable -  yes") {
    val keywordDaily = new KeywordDaily(spark, s3Location,"2019-06-01")
    assert(keywordDaily.isDataAvailable == true)
  }

  test("Test isDataAvailable -  no") {
    val keywordDaily = new KeywordDaily(spark, s3Location,"2018-06-01")
    assert(keywordDaily.isDataAvailable == false)
  }

  test("load - data is available") {
    val keywordDaily = new KeywordDaily(spark, s3Location,"2019-06-01")
    keywordDaily.dataAvailable = true
    val df: DataFrame = keywordDaily.load()
    assert(df.count() > 0)
  }

  test("load - data is not available") {
    val keywordDaily = new KeywordDaily(spark, s3Location, "2019-06-01")
    keywordDaily.dataAvailable = false
    val df: DataFrame = keywordDaily.load()
    assert(df.count() == 0)
  }

  test("load - validate lob and  count") {
    val keywordDaily = new KeywordDaily(spark, s3Location, "2019-06-01")
    keywordDaily.dataAvailable = true
    var df: DataFrame = keywordDaily.load()

    var dfMap: Map[String, Long] = df.groupBy(col("lob")).count()
                                      .collect().map(x => (x.getString(0),x.getLong(1))).toMap
    assert(dfMap("LX") == 8)
    assert(dfMap("Car") == 5)
    assert(dfMap("Hotel") == 20)
    assert(dfMap("Package") == 5)
    assert(dfMap("Flight") == 5)

    dfMap = df.groupBy(col("keyword_type")).count()
                .collect().map(x => (x.getString(0),x.getLong(1))).toMap
    assert(dfMap("LM") == 5)
    assert(dfMap("AT") == 5)
    assert(dfMap("PT") == 5)
    assert(dfMap("OD") == 5)
    assert(dfMap("DT") == 23)
  }
}
