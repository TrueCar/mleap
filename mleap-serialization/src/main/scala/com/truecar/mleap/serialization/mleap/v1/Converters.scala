package com.truecar.mleap.serialization.mleap.v1

import com.truecar.mleap.core.linalg
import com.truecar.mleap.runtime
import com.truecar.mleap.runtime.types.{DoubleType, StringArrayType, StringType, VectorType}
import com.truecar.mleap.runtime.{LocalDataset, LocalLeapFrame, types}
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

/**
  * Created by hwilkins on 3/7/16.
  */
trait Converters {
  import scala.language.implicitConversions

  implicit def mleapDenseVectorToProtoMleap(vector: linalg.DenseVector): DenseVector = {
    DenseVector(vector.values)
  }
  implicit def protoMleapDenseVectorToMleap(vector: DenseVector): linalg.DenseVector = {
    linalg.DenseVector(vector.values.toArray)
  }

  implicit def mleapSparseVectorToProtoMleap(vector: linalg.SparseVector): SparseVector = {
    SparseVector(vector.size,
      vector.indices,
      vector.values)
  }
  implicit def protoMleapSparseVectorToMleap(vector: SparseVector): linalg.SparseVector = {
    linalg.SparseVector(vector.size,
      vector.indices.toArray,
      vector.values.toArray)
  }

  implicit def mleapVectorToProtoMleap(vector: linalg.Vector): Vector = vector match {
    case vector: linalg.DenseVector => Vector(Vector.Data.Dense(vector))
    case vector: linalg.SparseVector => Vector(Vector.Data.Sparse(vector))
  }
  implicit def protoMleapVectorToMleap(vector: Vector): linalg.Vector = {
    if(vector.data.isDense) {
      vector.data.dense.get
    } else {
      vector.data.sparse.get
    }
  }

  implicit def mleapDataTypeToProtoMleap(dataType: types.DataType): DataType = dataType match {
    case DoubleType => DataType.DOUBLE
    case StringType => DataType.STRING
    case StringArrayType => DataType.STRING_ARRAY
    case VectorType => DataType.VECTOR
  }

  implicit def protoMleapDataTypeToMleap(dataType: DataType): types.DataType = {
    if(dataType.isDouble) { DoubleType }
    else if(dataType.isString) { StringType }
    else if(dataType.isStringArray) { StringArrayType }
    else if(dataType.isVector) { VectorType }
    else { throw new Error("Could not convert data type") }
  }

  implicit def mleapStructFieldToProtoMleap(field: types.StructField): StructField = {
    StructField(field.name, field.dataType)
  }

  implicit def protoMleapStructFieldToMleap(field: StructField): types.StructField = {
    types.StructField(field.name, field.dataType)
  }

  implicit def mleapStructTypeToProtoMleap(schema: types.StructType): StructType = {
    StructType(schema.fields.map(mleapStructFieldToProtoMleap))
  }

  implicit def protoMleapStructTypeToMleap(schema: StructType): types.StructType = {
    types.StructType(schema.fields.map(protoMleapStructFieldToMleap))
  }

  implicit def mleapFieldDataToProtoMleap(data: Any): FieldData = data match {
    case data: Double => FieldData(FieldData.Data.DoubleValue(data))
    case data: String => FieldData(FieldData.Data.StringValue(data))
    case data: Array[String] => FieldData(FieldData.Data.StringArrayValue(StringArray(data)))
    case data: linalg.Vector => FieldData(FieldData.Data.VectorValue(data))
    case _ => throw new Error("Could not convert field data to proto MLeap")
  }

  implicit def protoMleapFieldDataToMleap(fieldData: FieldData): Any = {
    val data = fieldData.data

    if(data.isDoubleValue) { data.doubleValue.get }
    else if(data.isStringValue) { data.stringValue.get }
    else if(data.isStringArrayValue) { data.stringArrayValue.get.strings }
    else if(data.isVectorValue) { protoMleapVectorToMleap(data.vectorValue.get) }
    else { throw new Error("Could not convert field to MLeap") }
  }

  implicit def mleapRowToProtoMleap(row: runtime.Row): Row = {
    val values = row.toArray.map(mleapFieldDataToProtoMleap)
    Row(values)
  }

  implicit def protoMleapRowToMleap(row: Row): runtime.Row = {
    val values = row.data.map(protoMleapFieldDataToMleap)
    runtime.Row(values.toArray)
  }

  implicit def mleapLeapFrameToProtoMleap(frame: LocalLeapFrame): LeapFrame = {
    LeapFrame(frame.schema, frame.dataset.data.map(mleapRowToProtoMleap))
  }

  implicit def protoMleapLeapFrameToMleap(frame: LeapFrame): LocalLeapFrame = {
    LocalLeapFrame(frame.schema, LocalDataset(frame.rows.map(protoMleapRowToMleap).toArray))
  }
}
object Converters extends Converters
