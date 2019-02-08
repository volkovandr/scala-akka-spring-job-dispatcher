package org.example.ScalaAkkaSpringTryout.helper

import org.slf4j.{Logger, LoggerFactory}

trait Logging {
  lazy val log: Logger = LoggerFactory.getLogger(getClass)
}
