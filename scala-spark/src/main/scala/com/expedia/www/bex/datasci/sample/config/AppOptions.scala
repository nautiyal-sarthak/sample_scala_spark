package com.expedia.www.bex.datasci.sample.config

case class AppOptions(
                       appName: String,
                       localMode: Boolean,
                       dataBucket: String,
                       dataFolder: String,
                       nestedConfig: NestedConfig
                     )

case class NestedConfig(
                       someValue: String
                       )