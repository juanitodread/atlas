package org.juanitodread.atlas

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn

object App {
  def main(args: Array[String]): Unit = {
    println("Starting server...")

    implicit val system = ActorSystem("atlas")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route = path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

    Http().bindAndHandle(route, "0.0.0.0", 8080)
    println("Server started at 0.0.0.0")
  }
}