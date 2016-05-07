package controllers


import services.Book

import scala.concurrent.{ExecutionContext, Future}
import akka.actor.ActorRef
import akka.util.Timeout
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.{Route, Directives}
import io.swagger.annotations._
import javax.ws.rs.Path

@Path("/book")
@Api(value = "/book", description = "application/json")
trait BooksDoc {

  @ApiOperation(value = "Return all books", notes = "", nickname = "listBooks", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return all books", response = classOf[List[Book]]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def listBook:Route


  @Path("/{id}")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "book id", required = true, dataType = "string", paramType = "path")
  ))
  @ApiOperation(value = "Return book by id", notes = "", nickname = "getBook", httpMethod = "GET")
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Return book", response = classOf[Book]),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def getBook:Route

  /*@ApiOperation(value = "Save Book", notes = "Return message as response", httpMethod = "POST", response = classOf[String])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "book object", required = true, dataType = "Book", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Book has been saved successfully"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def postBook:Route*/
}
