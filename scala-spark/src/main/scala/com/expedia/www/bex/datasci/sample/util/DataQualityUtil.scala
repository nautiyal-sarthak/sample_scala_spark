package com.expedia.www.bex.datasci.sample.util

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._


class DataQualityUtil {

  def doDQCheck(currentDF: DataFrame, historicalDF: DataFrame, columnNames: List[String]) : DataFrame = {

    val sums = columnNames.map(colName => sum(colName).as(colName)) :+ count(lit(1)).alias("cnt")
    val historicalSumsDF: DataFrame = historicalDF.groupBy("the_date").agg(sums.head, sums.tail:_*).cache()

    val avgs = (columnNames :+ "cnt").map(colName => avg(colName).as(colName))
    var historicalAvgsDF: DataFrame = historicalSumsDF.groupBy()
                                                      .agg(avgs.head, avgs.tail:_*)
                                                      .withColumn("type", lit("summary"))
    historicalAvgsDF = meltDF(historicalAvgsDF,
                            Seq("type"), columnNames :+ "cnt", "features", "mean")


    val stdDevs = (columnNames :+ "cnt").map(colName => stddev(colName).as(colName))
    var historicalStdDevDF: DataFrame = historicalSumsDF.groupBy()
                                                        .agg(stdDevs.head, stdDevs.tail:_*)
                                                        .withColumn("type", lit("summary"))
    historicalStdDevDF = meltDF(historicalStdDevDF,
                              Seq("type"), columnNames :+ "cnt", "features", "stddev")

    val histDF = historicalAvgsDF.join(historicalStdDevDF, Seq("type", "features"))


    var currentSumsDF: DataFrame = currentDF.groupBy("the_date")
                                            .agg(sums.head, sums.tail:_*)
                                            .withColumn("type", lit("summary"))

    currentSumsDF = meltDF(currentSumsDF, Seq("type"), columnNames :+ "cnt", "features", "curr")

    //TODO need more unit test coverage for this block
    val summaryDF: DataFrame = currentSumsDF.join(histDF, Seq("type", "features"))
                                            .withColumn("avgLB", expr("round(mean * 0.5,0)"))
                                            .withColumn("avgUB", expr("round(mean * 2,0)"))
                                            .withColumn("stdLB", expr("round(if(mean - stddev * 3 < 0, 0, mean - stddev * 3),0)"))
                                            .withColumn("stdUB", expr("round(mean + stddev * 3,0)"))
                                            .withColumn("LB", expr("if(avgLB > stdLB, stdLB, avgLB)"))
                                            .withColumn("UB", expr("if(avgUB > stdUB, avgUB, stdUB)"))
                                            .withColumn("status", expr("case when curr >= LB and curr <= UB and curr!=0 then 'pass' else 'fail' end"))
                                            .withColumn("mean", expr("round(mean,0)"))
                                            .select("features", "status", "curr", "mean", "LB", "UB")
    summaryDF
  }


  def meltDF(df: DataFrame,
            id_vars: Seq[String],
            value_vars: Seq[String],
            var_name: String,
            value_name: String) : DataFrame = {

    // Create array<struct<variable: str, value: ...>>
    val _vars_and_vals = array((for (c <- value_vars) yield { struct(lit(c).alias(var_name), col(c).alias(value_name)) }): _*)

    // Add to the DataFrame and explode
    val _tmp = df.withColumn("_vars_and_vals", explode(_vars_and_vals))

    val cols = id_vars.map(col _) ++ { for (x <- List(var_name, value_name)) yield { col("_vars_and_vals")(x).alias(x) }}

    return _tmp.select(cols: _*)

  }
}
