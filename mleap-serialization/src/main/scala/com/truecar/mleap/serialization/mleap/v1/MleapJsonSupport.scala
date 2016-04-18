package com.truecar.mleap.serialization.mleap.v1

import com.truecar.mleap.core.linalg
import com.truecar.mleap.runtime
import com.truecar.mleap.runtime.{LocalLeapFrame, types}
import com.truecar.mleap.serialization.mleap.v1.Converters._
import ml.bundle.support.ConversionJsonFormat._
import mleap.core.linalg.DenseVector.DenseVector
import mleap.core.linalg.SparseVector.SparseVector
import mleap.core.linalg.Vector.Vector
import mleap.runtime.FieldData.FieldData
import mleap.runtime.LeapFrame.LeapFrame
import mleap.runtime.Row.Row
import mleap.runtime.StringArray.StringArray
import mleap.runtime.types.DataType.DataType
import mleap.runtime.types.StructField.StructField
import mleap.runtime.types.StructType.StructType
import spray.json.DefaultJsonProtocol._
import spray.json._


/**
  * Created by hwilkins on 3/7/16.
  */
trait MleapJsonSupport {
  private implicit val protoMleapSparseVectorFormat = jsonFormat3(SparseVector.apply)
  implicit val protoMleapVectorFormat = new JsonFormat[Vector] {
    override def write(obj: Vector): JsValue = {
      if(obj.data.isDense) {
        obj.data.dense.get.values.toJson
      } else {
        obj.data.sparse.get.toJson
      }
    }
    override def read(json: JsValue): Vector = json match {
      case JsArray(jsValues) =>
        val values = jsValues.map {
          case JsNumber(value) => value.toDouble
          case _ => throw new Error("Could not read dense vector")
        }
        Vector(Vector.Data.Dense(DenseVector(values)))
      case json: JsObject =>
        val sparse = json.convertTo[SparseVector]
        Vector(Vector.Data.Sparse(SparseVector(sparse.size, sparse.indices, sparse.values)))
      case _ => throw new Error("Could not read vector")
    }
  }
  implicit val mleapVectorFormat: JsonFormat[linalg.Vector] = protoMleapVectorFormat

  implicit val protoMleapDataTypeFormat = new JsonFormat[DataType] {
    override def write(obj: DataType): JsValue = obj match {
      case DataType.DOUBLE => JsString("double")
      case DataType.STRING => JsString("string")
      case DataType.STRING_ARRAY => JsString("string_array")
      case DataType.VECTOR => JsString("vector")
      case _ => throw new Error("Could not serialize data type")
    }
    override def read(json: JsValue): DataType = json match {
      case JsString("double") => DataType.DOUBLE
      case JsString("string") => DataType.STRING
      case JsString("string_array") => DataType.STRING_ARRAY
      case JsString("vector") => DataType.VECTOR
      case _ => throw new Error("Could not read data type")
    }
  }
  implicit val mleapDataTypeFormat: JsonFormat[types.DataType] = protoMleapDataTypeFormat
  implicit val protoMleapStructFieldFormat = jsonFormat2(StructField.apply)
  implicit val mleapStructFieldFormat: RootJsonFormat[types.StructField] = protoMleapStructFieldFormat
  implicit val protoMleapStructTypeFormat = jsonFormat1(StructType.apply)
  implicit val mleapStructTypeFormat: RootJsonFormat[types.StructType] = protoMleapStructTypeFormat

  implicit val protoMleapFieldDataFormat = new JsonFormat[FieldData] {
    override def write(obj: FieldData): JsValue = {
      if(obj.data.isDoubleValue) {
        JsNumber(obj.data.doubleValue.get)
      } else if(obj.data.isStringValue) {
        JsString(obj.data.stringValue.get)
      } else if(obj.data.isStringArrayValue) {
        val values = obj.data.stringArrayValue.get.strings.map(JsString.apply)
        JsArray(values: _*)
      } else if(obj.data.isVectorValue) {
        obj.data.vectorValue.get.toJson
      } else {
        throw new Error("Could not serialize field data")
      }
    }
    override def read(json: JsValue): FieldData = json match {
      case JsNumber(num) => FieldData(FieldData.Data.DoubleValue(num.toDouble))
      case JsString(str) => FieldData(FieldData.Data.StringValue(str))
      case JsArray(jsValues) =>
        val values = jsValues.toSeq.map {
          case JsString(str) => str
          case _ => "Could not deserialize string array"
        }
        FieldData(FieldData.Data.StringArrayValue(StringArray(values)))
      case json: JsObject => FieldData(FieldData.Data.VectorValue(json.convertTo[Vector]))
      case _ => throw new Error("Could not deserialize field data")
    }
  }
  implicit val mleapFieldDataFormat: JsonFormat[Any] = protoMleapFieldDataFormat
  implicit val protoMleapRowFormat = new JsonFormat[Row] {
    override def write(obj: Row): JsValue = {
      val values = obj.data.map(protoMleapFieldDataFormat.write)
      JsArray(values: _*)
    }

    override def read(json: JsValue): Row = json match {
      case JsArray(jsValues) =>
        val values = jsValues.map(protoMleapFieldDataFormat.read)
        Row(values)
      case _ => throw new Error("Could not read row")
    }
  }
  implicit val mleapRowFormat: JsonFormat[runtime.Row] = protoMleapRowFormat
  implicit val protoMleapLeapFrameFormat = jsonFormat2(LeapFrame.apply)
  implicit val mleapLeapFrameFormat: RootJsonFormat[LocalLeapFrame] = protoMleapLeapFrameFormat
}
object MleapJsonSupport extends MleapJsonSupport
