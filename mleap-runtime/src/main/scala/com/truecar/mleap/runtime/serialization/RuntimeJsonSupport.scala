package com.truecar.mleap.runtime.serialization

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.core.serialization.{MleapJsonProtocol, CoreJsonSupport}
import com.truecar.mleap.runtime.types.{DataType, StructField, StructType}
import com.truecar.mleap.runtime._
import spray.json._

import scala.language.implicitConversions


/**
  * Created by hwilkins on 11/12/15.
  */
trait RuntimeJsonSupport extends BaseRuntimeJsonSupport with TransformerJsonSupport
object RuntimeJsonSupport extends RuntimeJsonSupport

trait BaseRuntimeJsonSupport extends MleapJsonProtocol with CoreJsonSupport {
  // Types

  implicit object MleapDataTypeFormat extends JsonFormat[DataType] {
    override def write(obj: DataType): JsValue = JsString(obj.typeName)
    override def read(json: JsValue): DataType = DataType.fromName(json)
  }

  implicit object MleapStructFieldFormat extends RootJsonFormat[StructField] {
    override def write(obj: StructField): JsValue = {
      JsObject(Map("name" -> JsString(obj.name), "dataType" -> obj.dataType.toJson))
    }

    override def read(json: JsValue): StructField = {
      val obj = json.asJsObject()
      val name: String = obj.fields("name")

      StructField(name, obj.fields("dataType").convertTo[DataType])
    }
  }

  implicit val mleapStructTypeFormat = jsonFormat[Seq[StructField], StructType](StructType.apply, "fields")

  implicit object MleapRowFormat extends RootJsonFormat[Row] {
    override def write(obj: Row): JsValue = {
      val values = obj.toArray.map {
        case value: Double => JsNumber(value)
        case value: String => JsString(value)
        case value: Vector => value.toJson
      }

      JsArray(values: _*)
    }

    override def read(json: JsValue): Row = json match {
      case JsArray(values) =>
        val data = values.map {
          case JsNumber(value) => value.toDouble
          case JsString(value) => value
          case value => value.convertTo[Vector]
        }
        Row(data.toArray)
      case value => throw new Error("Invalid JSON Row format: " + value)
    }
  }

  implicit val mleapArrayDatasetFormat = jsonFormat1(LocalDataset.apply)
  implicit val mleapLocalLeapFrameFormat = jsonFormat2(LocalLeapFrame.apply)
}
object BaseRuntimeJsonSupport extends BaseRuntimeJsonSupport