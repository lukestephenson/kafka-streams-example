package com.luke

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.errors.InvalidStateStoreException
import org.apache.kafka.streams.state.{QueryableStoreTypes, ReadOnlyKeyValueStore}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends StrictLogging {
  val StateStoreName = "StateStoreName"

  var streams: KafkaStreams = null

  def main(args: Array[String]): Unit = {
    Logging.initLogger()

    streams = new GlobalStateStoreExample().streams

    streams.start()

    Future {
      pollTheStateStore()
    }
  }

  private def pollTheStateStore(): Unit = {
    try {
      Thread.sleep(10000)
      logger.info("Polling GlobalKTable")

      val keyValueStore: ReadOnlyKeyValueStore[String, Long] =
        streams.store(StateStoreName, QueryableStoreTypes.keyValueStore[String, Long]())

      while(true) {
        println()
        println()
        logger.info("in loop")
        keyValueStore.all().forEachRemaining { foo =>
          logger.info(s"got $foo")
        }

        Thread.sleep(10000)
      }
    } catch {
      case t: InvalidStateStoreException =>
        logger.error("Failed to poll GlobalKTable.  Retrying")
        Thread.sleep(1000)
        pollTheStateStore()
      case t: Throwable =>
        logger.error("Failed to poll GlobalKTable. Giving up", t)
        t.printStackTrace()
    }
  }

}
