package com.luke

import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory

object Logging {
  def initLogger() = {
    val rootLogger = LoggerFactory.getLogger("root").asInstanceOf[Logger]
    rootLogger.setLevel(Level.INFO)
  }
}
