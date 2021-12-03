package com.expedia.www.bex.datasci.sample.config

import org.slf4j.{Logger, LoggerFactory}
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

object AppOptionsUtil {
  private lazy val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * Load options from conf file
   * @param args
   * @return
   */
  def loadOptions(args: Array[String]): Option[AppOptions] = {
    // To have both field names and configuration files in camelCase (default KebabCase)
    implicit def hint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

    if (args.isEmpty) { // no args
      LOGGER.error("Env parameter must be provided. Please use test, staging or prod")
      None
    }
    else {
      args(0) match {
        case env:String if (env.matches("test|staging|prod")) =>
          ConfigSource.resources(s"options/${env}.conf").load[AppOptions] match {
            case Right(appOptions) => Some(appOptions)
            case Left(configReaderFailures) => {
              LOGGER.error(s"Failed to read config. ${configReaderFailures.head}")
              None
            }
          }
        case unknown: String => {
          LOGGER.error(s"Unknown env parameter: ${unknown}. Please use test, staging or prod")
          None
        }
      }
    }

  }
}