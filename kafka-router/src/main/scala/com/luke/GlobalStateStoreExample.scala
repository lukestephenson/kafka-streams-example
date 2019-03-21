package com.luke

import java.util.Properties

import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.streams.processor.{Processor, ProcessorContext, ProcessorSupplier}
import org.apache.kafka.streams.scala.Serdes
import org.apache.kafka.streams.state.{KeyValueStore, StoreBuilder, Stores}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig, Topology}

final class GlobalStateStoreExample() extends StrictLogging {

  private val props: Properties = {
    val _props = new Properties()
    _props.put(StreamsConfig.APPLICATION_ID_CONFIG, "example-1")
    _props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka.docker:9092")
    _props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "1")
    _props.put(ConsumerConfig.GROUP_ID_CONFIG, "example-1")
    _props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    _props
  }

  private val topology: Topology = {

    val processor: Processor[String, String] = new Processor[String, String] {
      var keyValueStore: KeyValueStore[String, Long] = null
      var globalContext: ProcessorContext = null

      override def init(context: ProcessorContext): Unit = {
        globalContext = context
        keyValueStore = context.getStateStore(Main.StateStoreName).asInstanceOf[KeyValueStore[String, Long]]
      }

      override def process(key: String, value: String): Unit = {
        println(s"put $key - $value")

        println(s"offset from context - ${globalContext.offset()}")

        keyValueStore.put(key, value.length.toLong)
      }

      override def close(): Unit = {}
    }

    val storeSupplier: StoreBuilder[KeyValueStore[String, Long]] = Stores.keyValueStoreBuilder(
      Stores.persistentKeyValueStore(Main.StateStoreName),
      Serdes.String,
      Serdes.Long)
      .withLoggingDisabled()

    val processorSupplier = new ProcessorSupplier[String, String] {
      override def get(): Processor[String, String] = processor
    }

    val topology = new Topology()

    topology.addGlobalStore(storeSupplier, "Source", new StringDeserializer(),
      new StringDeserializer(), "test.topic", "Processor", processorSupplier)

    topology
  }

  val streams: KafkaStreams = {
    val _streams = new KafkaStreams(topology, props)
    _streams.setUncaughtExceptionHandler((thread, throwable) => {
      logger.error(s"Thread: ${thread.toString}", throwable)
    })
    _streams
  }
}
