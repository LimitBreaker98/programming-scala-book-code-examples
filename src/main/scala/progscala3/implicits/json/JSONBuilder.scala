// src/main/scala/progscala3/implicits/json/JSONBuilder.scala

package progscala3.implicits.json
import scala.collection.mutable.ArrayBuffer

@main def JSONBuilderExample(): Unit =
  val js = JSON {
    "config" -> map {
      "master" -> map {
        "host" -> "192.168.1.1"
        "port" -> 8000
        "security" -> "null"
        // "foo" -> (1, 2.2, "three")  // doesn't compile!
      }
      "nodes" -> array {
        elem {
          "name" -> "node1"
          "host" -> "192.168.1.10"
        }
        elem {
          "name" -> "node2"
          "host" -> "192.168.1.20"
        }
      }
    }
  }
  println(js)

object JSONElement:                                                  // <1>
  def str[T](t: T): String = t match
    case "null" => "null"
    case s: String => "\""+s+"\""
    case _ => t.toString

sealed trait JSONElement                                             // <2>
case class JSONNamedElement[T](name: String, element: T) extends JSONElement:
  override def toString = "\""+name+"\": "+JSONElement.str(element)
case class JSONArrayElement[T](element: T) extends JSONElement:
  override def toString = JSONElement.str(element)

trait JSONContainer extends JSONElement:                             // <3>
  val elements = new ArrayBuffer[JSONElement]
  def add(e: JSONElement): Unit = elements += e
  def open: String
  def close: String
  override def toString = elements.mkString(open, ", ", close)

class JSONObject extends JSONContainer:                              // <4>
  val open: String  = "{"
  val close: String = "}"
class JSONArray extends JSONContainer:                               // <5>
  val open: String  = "["
  val close: String = "]"

sealed trait ValidJSONValue[T]                                       // <6>
implicit object VJSONInt     extends ValidJSONValue[Int]
implicit object VJSONDouble  extends ValidJSONValue[Double]
implicit object VJSONString  extends ValidJSONValue[String]
implicit object VJSONBoolean extends ValidJSONValue[Boolean]
implicit object VJSONObject  extends ValidJSONValue[JSONObject]
implicit object VJSONArray   extends ValidJSONValue[JSONArray]

extension [T : ValidJSONValue] (name: String)
  def ->(element: T)(using jc: JSONContainer) =
    jc.add(JSONNamedElement(name, element))

def JSON(init: JSONObject ?=> Unit) =                                // <7>
  given jo as JSONObject
  init
  jo

def map(init: JSONObject ?=> Unit) =                                 // <8>
  given jo as JSONObject
  init
  jo
def elem(init: JSONObject ?=> Unit)(using jc: JSONContainer) =       // <9>
  given jo as JSONObject
  init
  jc.add(jo)

def array(init: JSONArray ?=> Unit) =                                // <10>
  given ja as JSONArray
  init
  ja
