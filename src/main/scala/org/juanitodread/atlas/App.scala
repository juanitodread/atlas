package org.juanitodread.atlas

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.util.Timeout
import org.juanitodread.atlas.api.RestApi
import org.juanitodread.atlas.iot.actor.IotSupervisor

import scala.io.StdIn

object App extends RequestTimeout {
  def main(args: Array[String]): Unit = {
    println("Starting the app...")
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
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(): Timeout = {
    val t = "30s" //config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}