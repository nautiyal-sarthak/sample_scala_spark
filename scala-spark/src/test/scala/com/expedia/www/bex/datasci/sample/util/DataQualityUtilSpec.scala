package com.expedia.www.bex.datasci.sample.util

import com.expedia.www.bex.datasci.sample.SparkJobSuiteRunner
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{StructField, _}

class DataQualityUtilSpec extends SparkJobSuiteRunner{

  val s3Location: String = "src/test/resources/data/"
  val theDate: String = "2019-06-06"
  val dataQualityUtil: DataQualityUtil = new DataQualityUtil()
  val columnNames: List[String] = List("impressions", "clicks", "conversions", "spend", "first_page_cpc_google",
                                        "search_impression_share", "search_exact_match_impression_share",
                                        "search_rank_lost_impression_share")

  test ("test doDQCheck") {
    val df: DataFrame = dataQualityUtil.doDQCheck(spark.read.option("header","true")
                                                            .csv(s3Location + "dataqualityutil/current.csv"),
                                                  spark.read.option("header","true")
                                                            .csv(s3Location + "dataqualityutil/historic.csv"),
                                                  columnNames)
    val arrRows = df.collect()
    assert(arrRows.length == 9)
    assert(arrRows(0).length == 6)

  }

  test ("meltDF") {
    val data = Seq(Row("type", 1, 2, 3, 4))
    val schema = List(StructField("type", StringType, true),
      StructField("impressions", IntegerType, true),
      StructField("clicks", IntegerType, true),
      StructField("conversions", IntegerType, true),
      StructField("spend", IntegerType, true)
    )

    val id_vars: String = "type"
    val value_vars: List[String] = List("impressions", "clicks", "conversions", "spend")
    var df: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(data), StructType(schema))
    df = dataQualityUtil.meltDF(df, Seq(id_vars), value_vars, "features", "sum")
    val arrRows = df.collect()
    assert(arrRows.length == 4)
    for (row <- arrRows) {
      assert(row.get(0).equals(id_vars))
      assert(value_vars.contains(row.get(1)))
    }
  }

}
