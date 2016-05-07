import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.DebuggingDirectives
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import controllers.{BooksDoc, Books}
import swagger.SwaggerDocService
import akka.http.scaladsl.server.Directives._

import scala.reflect.runtime.{universe => ru}

/**
 * Created by mayank on 20/4/16.
 */
object Boot extends App with CORSSupport {
  implicit val system = ActorSystem("system-a")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val config = ConfigFactory.load()
  val logger = Logging(system, getClass)


  val swaggerDocService = new SwaggerDocService("localhost", 8080, system, Seq(ru.typeOf[BooksDoc]))

  val routesWithSwagger = swaggerDocService.apiviewerRoutes ~ swaggerDocService.routes ~ ((new Books).routes)
  //val routesWithLoging = DebuggingDirectives.logRequestResult("Greeting-Api", Logging.InfoLevel)(routesWithSwagger)

  Http().bindAndHandle(corsHandler(routesWithSwagger), "localhost", 8080)
}



trait CORSSupport {

  final val OK_RESPONSE = 200

  lazy val allowedOriginHeader = {
    val config = ConfigFactory.load()
    val sAllowedOrigin = "*" //TODO should be moved into application.conf and be used like "config.getString("cors.allowed-origin")"
    if (sAllowedOrigin == "*"){
      `Access-Control-Allow-Origin`.*
    }
    else{
      `Access-Control-Allow-Origin`(HttpOrigin(sAllowedOrigin))
    }
  }

  private def addAccessControlHeaders: Directive0 = {
    mapResponseHeaders { headers =>
      allowedOriginHeader +:
        `Access-Control-Allow-Credentials`(true) +:
        `Access-Control-Allow-Headers`("Token", "Content-Type", "X-Requested-With", "Authorization") +:
        headers
    }
  }

  private def preFreebirdRequestHandler: Route = options {
    complete(HttpResponse(OK_RESPONSE).withHeaders(
      `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)
    )
    )
  }

  def corsHandler(r: Route): Route = addAccessControlHeaders {
    preFreebirdRequestHandler ~ r
  }
}