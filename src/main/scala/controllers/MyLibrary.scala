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
import services.{LibraryService, Book}
import utils.JsonHelpers

/**
 * Created by mayank on 23/3/16.
 */

trait MyLibrary extends JsonHelpers{

  val library = new LibraryService

  val routes: Route = {
    get {
      pathSingleSlash {
        complete {
          HttpResponse(OK, entity = "Welcome to library")
        }
      } ~
        path("book") {
          complete {
            val books = library.list
              HttpResponse(OK, entity = toJson(books))
          }
        } ~
        path("book" / Segment) { bookId =>
          complete {
            val book = library.fetch(bookId)
            if(book.isDefined)
            HttpResponse(OK, entity = toJson(book))
            else
              HttpResponse(BadRequest, entity = s"Book Not Found")

          }
        }
    } ~
      post {
        path("book") {
          entity(as[String]) { book =>
            complete {
              if (library.save(toObject(book).extract[Book]))
                HttpResponse(OK, entity = s"Book has been stored")
              else {
                HttpResponse(BadRequest, entity = s"Error while saving request")
              }
            }
          }
        }
      }
  }
}


object InitService extends App with MyLibrary {
  implicit val system = ActorSystem("system-a")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)
  val routesWithLoging = DebuggingDirectives.logRequestResult("Greeting-Api", Logging.InfoLevel)(routes)
  Http().bindAndHandle(routesWithLoging, "localhost", 8080)
}
