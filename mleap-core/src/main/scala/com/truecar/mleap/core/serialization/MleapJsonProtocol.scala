package com.truecar.mleap.core.serialization

import com.truecar.mleap.core.util.Atom
import spray.json._

/**
  * Created by hwilkins on 11/20/15.
  */
trait TypeName {
  def typeName: String
}

case class MultiFormat[T <: TypeName](map: Map[String, TypedFormat[T]]) extends RootJsonFormat[T] {
  override def read(json: JsValue): T = {
    json.asJsObject.fields("type") match {
      case JsString(typeName) =>
        val format = map(typeName)
        format.read(json)
      case _ => throw new Error("Could not find type fields")
    }
  }

  override def write(obj: T): JsValue = map(obj.typeName).write(obj)

  def withFormat(typeName: String, format: BasicTypedFormat[T]): MultiFormat[T] = {
    copy(map = map + (typeName -> format))
  }
}

trait TypedFormat[T <: TypeName] extends RootJsonFormat[T]

case class LiftedTypedFormat[S <: TypeName, T <: S](format: TypedFormat[T]) extends TypedFormat[S] {
  override def read(json: JsValue): S = {
    format.read(json)
  }

  override def write(obj: S): JsValue = {
    format.write(obj.asInstanceOf[T])
  }
}

case class BasicTypedFormat[T <: TypeName](format: RootJsonFormat[T]) extends TypedFormat[T] {
  override def read(json: JsValue): T = format.read(json)

  override def write(obj: T): JsValue = {
    val fields = ("type", JsString(obj.typeName)) +: format.write(obj).asJsObject.fields.toSeq
    JsObject(fields.toMap)
  }
}

case class AtomFormat[T, F <: RootJsonFormat[T]](atom: Atom[F]) extends RootJsonFormat[T] {
  override def read(json: JsValue): T = atom.get.read(json)
  override def write(obj: T): JsValue = atom.get.write(obj)
}

trait MleapJsonProtocol extends DefaultJsonProtocol {
  import scala.language.implicitConversions

  protected implicit def extractString(json: JsValue): String = json match {
    case JsString(value) => value
    case value => throw new Error("invalid string: " + value)
  }

  protected implicit def extractInt(json: JsValue): Int = json match {
    case JsNumber(value) => value.toInt
    case value => throw new Error("invalid int: " + value)
  }

  protected implicit def extractDouble(json: JsValue): Double = json match {
    case JsNumber(value) => value.toDouble
    case value => throw new Error("invalid double: " + value)
  }

  protected implicit def extractIntArray(json: JsValue): Array[Int] = json match {
    case JsArray(values) => values.map(value => extractInt(value)).toArray
    case value => throw new Error("invalid int array: " + value)
  }

  protected implicit def extractDoubleArray(json: JsValue): Array[Double] = json match {
    case JsArray(values) => values.map(value => extractDouble(value)).toArray
    case value => throw new Error("invalid double array: " + value)
  }

  def multiFormat[T <: TypeName](map: Map[String, TypedFormat[T]]): MultiFormat[T] = {
    MultiFormat(map)
  }

  def atomFormat[T, F <: RootJsonFormat[T]](format: F): AtomFormat[T, F] = {
    AtomFormat(Atom(format))
  }

  implicit def typedFormat[T <: TypeName](format: RootJsonFormat[T]): BasicTypedFormat[T] = BasicTypedFormat(format)
  implicit def liftedTypedFormat[S <: TypeName, T <: S](format: TypedFormat[T]): LiftedTypedFormat[S, T] = {
    LiftedTypedFormat(format)
  }
}
object MleapJsonProtocol extends MleapJsonProtocol
