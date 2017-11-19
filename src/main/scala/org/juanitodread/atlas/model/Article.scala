package org.juanitodread.atlas.model

import java.util.Date

import akka.actor.{ Actor, ActorLogging, Props }
import akka.util.Timeout

import scala.collection.mutable.ArrayBuffer

object ArticleWriter {

  def props() = Props(new ArticleWriter)
  def name() = "articleWriter"

  case class Article(
    title: String,
    content: String,
    url: String,
    created: Long
  )

  case class WriteArticle(article: Article)
  case object GetArticles

}

class ArticleWriter extends Actor with ActorLogging {
  import org.juanitodread.atlas.model.ArticleWriter._

  val articlesStore = ArrayBuffer.empty[Article]
  articlesStore += Article("titulo 1", "content 1", "http", new Date().getTime)
  articlesStore += Article("titulo 2", "content 2", "http", new Date().getTime)

  override def receive: Receive = {
    case WriteArticle(article) => articlesStore += article
    case GetArticles => {
      println(s"Thread: ${self.path.name} - Thread: ${self.path}")
      sender() ! articlesStore
    }
  }

}