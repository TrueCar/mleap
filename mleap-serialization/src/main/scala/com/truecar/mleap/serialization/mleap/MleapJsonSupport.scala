package com.truecar.mleap.serialization.mleap

import com.truecar.mleap.core.linalg
import com.truecar.mleap.runtime
import com.truecar.mleap.runtime.{LocalLeapFrame, types}
import mleap.runtime.FieldData.FieldData
import mleap.runtime.LeapFrame.LeapFrame
import mleap.runtime.Row.Row
import mleap.runtime.StringArray.StringArray
import mleap.runtime.types.DataType.DataType
import mleap.runtime.types.StructField.StructField
import mleap.runtime.types.StructType.StructType
import spray.json.DefaultJsonProtocol._
import mleap.core.linalg.DenseVector.DenseVector
import mleap.core.linalg.SparseVector.SparseVector
import mleap.core.linalg.Vector.Vector
import spray.json._
import Converters._

/**
  * Created by hwilkins on 3/7/16.
  */
case class ConvertRootJsonFormat[Mleap, ProtoMleap](format: RootJsonFormat[ProtoMleap])
                                                   (implicit toProtoMleap: (Mleap) => ProtoMleap,
                                                    fromProtoMleap: (ProtoMleap) => Mleap) extends RootJsonFormat[Mleap] {
  override def write(obj: Mleap): JsValue = format.write(obj)
  override def read(json: JsValue): Mleap = format.read(json)
}

case class ConvertJsonFormat[Mleap, ProtoMleap](format: JsonFormat[ProtoMleap])
                                               (implicit toProtoMleap: (Mleap) => ProtoMleap,
                                                fromProtoMleap: (ProtoMleap) => Mleap) extends JsonFormat[Mleap] {
  override def write(obj: Mleap): JsValue = format.write(obj)
  override def read(json: JsValue): Mleap = format.read(json)
}

trait MleapJsonSupport {
  def convertFormat[Mleap, ProtoMleap](format: RootJsonFormat[ProtoMleap])
                                      (implicit toProtoMleap: (Mleap) => ProtoMleap,
                                       fromProtoMleap: (ProtoMleap) => Mleap): ConvertRootJsonFormat[Mleap, ProtoMleap] = {
    ConvertRootJsonFormat(format)
  }

  def convertFormat[Mleap, ProtoMleap](format: JsonFormat[ProtoMleap])
                                      (implicit toProtoMleap: (Mleap) => ProtoMleap,
                                       fromProtoMleap: (ProtoMleap) => Mleap): ConvertJsonFormat[Mleap, ProtoMleap] = {
    ConvertJsonFormat(format)
  }

  implicit val protoMleapDenseVectorFormat = jsonFormat1(DenseVector.apply)
  implicit val mleapDenseVectorFormat = convertFormat[linalg.DenseVector, DenseVector](protoMleapDenseVectorFormat)
  implicit val protoMleapSparseVectorFormat = jsonFormat3(SparseVector.apply)
  implicit val mleapSparseVectorFormat = convertFormat[linalg.SparseVector, SparseVector](protoMleapSparseVectorFormat)
  implicit val protoMleapVectorFormat = new RootJsonFormat[Vector] {
    override def write(obj: Vector): JsValue = {
      if(obj.data.isDense) {
        obj.data.dense.get.toJson
      } else {
        obj.data.sparse.get.toJson
      }
    }
    override def read(json: JsValue): Vector = {
      val fields = json.asJsObject.fields

      if(fields.contains("size")) {
        Vector(Vector.Data.Sparse(json.convertTo[SparseVector]))
      } else {
        Vector(Vector.Data.Dense(json.convertTo[DenseVector]))
      }
    }
  }
  implicit val mleapVectorFormat = convertFormat[linalg.Vector, Vector](protoMleapVectorFormat)

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
  implicit val mleapDataTypeFormat = convertFormat[types.DataType, DataType](protoMleapDataTypeFormat)
  implicit val protoMleapStructFieldFormat = jsonFormat2(StructField.apply)
  implicit val mleapStructFieldFormat = convertFormat[types.StructField, StructField](protoMleapStructFieldFormat)
  implicit val protoMleapStructTypeFormat = jsonFormat1(StructType.apply)
  implicit val mleapStructTypeFormat = convertFormat[types.StructType, StructType](protoMleapStructTypeFormat)

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
  implicit val mleapFieldDataFormat = convertFormat[Any, FieldData](protoMleapFieldDataFormat)(mleapFieldDataToProtoMleap, protoMleapFieldDataToMleap)
  implicit val protoMleapRowFormat = jsonFormat1(Row.apply)
  implicit val mleapRowFormat = convertFormat[runtime.Row, Row](protoMleapRowFormat)
  implicit val protoMleapLeapFrameFormat = jsonFormat2(LeapFrame.apply)
  implicit val mleapLeapFrameFormat = convertFormat[LocalLeapFrame, LeapFrame](protoMleapLeapFrameFormat)
}
object MleapJsonSupport extends MleapJsonSupport
