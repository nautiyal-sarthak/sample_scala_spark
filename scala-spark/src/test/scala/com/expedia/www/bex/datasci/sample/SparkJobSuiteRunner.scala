package com.expedia.www.bex.datasci.sample

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, SharedSparkContext, SparkContextProvider}
import org.apache.spark.SparkConf
import org.scalatest._

trait SparkJobSuiteRunner extends FunSuite with Matchers with GivenWhenThen with BeforeAndAfter
  with SharedSparkContext with SparkContextProvider with DataFrameSuiteBase{

  override def conf: SparkConf = {
    new SparkConf().
      setMaster("local[*]").
      setAppName("unittestapp").
      set("spark.driver.host", "localhost").
      set("spark.ui.port", "8001")
    }
}