package controllers

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import services.{BookService, Book}
import utils.JsonHelpers

/**
 * Created by mayank on 23/3/16.
 */

class Books extends JsonHelpers with BooksDoc {

  val bookService = new BookService

  val routes = getWelcomeMsg ~ listBook ~ getBook ~ postBook

  def getWelcomeMsg = get {
    pathSingleSlash {
      complete {
        HttpResponse(OK, entity = "Welcome to library")
      }
    }
  }

  def listBook:Route = get {
    path("book") {
      complete {
        val books = bookService.list
        HttpResponse(OK, entity = toJson(books))
      }
    }
  }

  def getBook:Route = get {
    path("book" / Segment) { bookId =>
      complete {
        val book = bookService.fetch(bookId)
        if (book.isDefined)
          HttpResponse(OK, entity = toJson(book))
        else
          HttpResponse(BadRequest, entity = s"Book Not Found")
      }
    }
  }

  def postBook:Route = post {
    path("book") {
      entity(as[String]) { book =>
        complete {
          if (bookService.save(toObject(book).extract[Book]))
            HttpResponse(OK, entity = s"Book has been stored")
          else {
            HttpResponse(BadRequest, entity = s"Error while saving request")
          }
        }
      }
    }
  }

}



