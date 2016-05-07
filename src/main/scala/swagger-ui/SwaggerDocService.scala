package swagger

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.stream.ActorMaterializer
import com.github.swagger.akka.model._
import com.github.swagger.akka.{HasActorSystem, SwaggerHttpService}
import controllers.BooksDoc
import scala.reflect.runtime.universe._
import scala.reflect.runtime.{universe => ru}


class SwaggerDocService(address: String, port: Int, system: ActorSystem, docClasses: Seq[Type]) extends SwaggerHttpService with HasActorSystem {
  override implicit val actorSystem: ActorSystem = system
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override val host = address + ":" + port
  override val info = Info(version = "1.0")
  override val apiTypes = docClasses
  override val basePath = "http://localhost:8080"


  val apiviewerRoutes =
     get {
      pathPrefix("swagger") {
        pathEndOrSingleSlash {
          getFromResource(s"swagger/index.html")
        }
      } ~
        getFromResourceDirectory("swagger")
    }




 /* def assets = pathPrefix("swagger") {
    getFromResourceDirectory("swagger") ~ pathSingleSlash(get(redirect("index.html", StatusCodes.PermanentRedirect))) } ~ routes*/
}
