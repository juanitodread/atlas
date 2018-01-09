package org.juanitodread.atlas.sample

import java.util.Properties

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.util.Timeout
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerConfig, ProducerRecord }
import org.juanitodread.atlas.api.RestApi
import org.juanitodread.atlas.iot.actor.IotSupervisor

import scala.io.StdIn

object App extends RequestTimeout {
  def main(args: Array[String]): Unit = {
    println("Starting the app...")
    kafkaProducerTest()
    startApi()
  }

  private def startIot(): Unit = {
    implicit val system = ActorSystem("iot-system")

    try {
      val supervisor = system.actorOf(IotSupervisor.props(), "iot-supervisor")

      println("Press ENTER to exit")

      StdIn.readLine()
    } finally system.terminate()
  }

  private def startApi(): Unit = {
    println("Starting server...")

    implicit val system = ActorSystem("atlas")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val api = new RestApi(system, requestTimeout()).routes()

    Http().bindAndHandle(api, "0.0.0.0", 8080)
    println("Server started at 0.0.0.0:80")
  }

  private def kafkaProducerTest(): Unit = {
    println("Starting Kafka producer test...")
    val props = new Properties()

    props.put("bootstrap.servers", "192.168.100.10:9092")
    props.put("client.id", "DemoProducer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[Long, String](props)

    val messageId = System.currentTimeMillis()
    val message = new ProducerRecord[Long, String]("test-topic", messageId, s"A new message was created with id: ${messageId}")
    val record = producer.send(message).get()
    println(f"A new record inserted into Kafka: ${record}")
  }
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(): Timeout = {
    val t = "30s" //config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}