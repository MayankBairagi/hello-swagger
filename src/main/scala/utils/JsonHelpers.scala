package utils


/**
 * Created by mayank on 30/3/16.
 */
trait JsonHelpers {

  import org.json4s._
  import org.json4s.native.JsonMethods._
  import org.json4s.native.Serialization._

  implicit val formats = DefaultFormats

  def toObject(jsonString: String): JValue = parse(jsonString)

  def toJson(any: AnyRef): String = write(any)
}
