package com.fridaynightbeer.util

import org.slf4j.{Logger, LoggerFactory}

/**
  * Logger trait.
  */
trait Logging {

  protected val logger: Logger = LoggerFactory.getLogger(getClass.getName.stripSuffix("$"))

}
