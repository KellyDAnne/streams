package kafkaStream

import java.lang.Override
import java.util.Properties
import java.util.concurrent.CountDownLatch

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object Pipe extends App{


  val props = new Properties()
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
  props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)

  val builder = new StreamsBuilder()
  builder.stream("topic-input").to("topic-output")

  val topology = builder.build()

  println(topology.describe())

  val streams = new KafkaStreams(topology, props)

  val latch = new CountDownLatch(1)

  Runtime.getRuntime.addShutdownHook(new Thread("streams-shutdown-hook"){
    override def run(){
      streams.close()
      latch.countDown()
    }
  })

  try{
    streams.start()
    latch.await()
  } catch {
    case e: Throwable =>  System.exit(1)
  }
  System.exit(0)

}