package org.juanitodread.atlas.api

import akka.actor.{ ActorRef, ActorSystem }
import akka.pattern.ask
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import org.juanitodread.atlas.model.ArticleWriter
import org.juanitodread.atlas.model.ArticleWriter.Article

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  override implicit def executionContext: ExecutionContext = system.dispatcher
  override implicit def requestTimeout = timeout
  override def createArticleWriter(): ActorRef = system.actorOf(ArticleWriter.props(), ArticleWriter.name())
}

trait RestRoutes extends ArticleWriterApi {

  def routes(): Route = articlesRoute

  def articlesRoute = Directives.pathPrefix("articles") {
    pathEndOrSingleSlash {
      get {
        onSuccess(getArticles()) { articles =>
          complete(OK, articles.toString)
        }
      }
    }
  }

}

trait ArticleWriterApi {
  import org.juanitodread.atlas.model.ArticleWriter._
  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val articleWriter: ActorRef = createArticleWriter()

  def createArticleWriter(): ActorRef

  def getArticles() = articleWriter.ask(GetArticles)

}