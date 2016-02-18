package org.apache.spark.ml.mleap

import com.truecar.mleap.core.linalg
import com.truecar.mleap.core.linalg.{DenseVector, SparseVector}
import org.apache.spark.annotation.AlphaComponent
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.GenericMutableRow
import org.apache.spark.sql.catalyst.util.GenericArrayData
import org.apache.spark.sql.types._

/**
  * Created by hwilkins on 11/17/15.
  */
@AlphaComponent
class VectorUDT extends UserDefinedType[linalg.Vector] {

  override def sqlType: StructType = {
    // type: 0 = sparse, 1 = dense
    // We only use "values" for dense vectors, and "size", "indices", and "values" for sparse
    // vectors. The "values" field is nullable because we might want to add binary vectors later,
    // which uses "size" and "indices", but not "values".
    StructType(Seq(
      StructField("type", ByteType, nullable = false),
      StructField("size", IntegerType, nullable = true),
      StructField("indices", ArrayType(IntegerType, containsNull = false), nullable = true),
      StructField("values", ArrayType(DoubleType, containsNull = false), nullable = true)))
  }

  override def serialize(obj: Any): InternalRow = {
    obj match {
      case SparseVector(size, indices, values) =>
        val row = new GenericMutableRow(4)
        row.setByte(0, 0)
        row.setInt(1, size)
        row.update(2, new GenericArrayData(indices.map(_.asInstanceOf[Any])))
        row.update(3, new GenericArrayData(values.map(_.asInstanceOf[Any])))
        row
      case DenseVector(values) =>
        val row = new GenericMutableRow(4)
        row.setByte(0, 1)
        row.setNullAt(1)
        row.setNullAt(2)
        row.update(3, new GenericArrayData(values.map(_.asInstanceOf[Any])))
        row
    }
  }

  override def deserialize(datum: Any): linalg.Vector = {
    datum match {
      case row: InternalRow =>
        require(row.numFields == 4,
          s"VectorUDT.deserialize given row with length ${row.numFields} but requires length == 4")
        val tpe = row.getByte(0)
        tpe match {
          case 0 =>
            val size = row.getInt(1)
            val indices = row.getArray(2).toIntArray()
            val values = row.getArray(3).toDoubleArray()
            new SparseVector(size, indices, values)
          case 1 =>
            val values = row.getArray(3).toDoubleArray()
            new DenseVector(values)
        }
    }
  }

  override def pyUDT: String = "pyspark.mllib.linalg.VectorUDT"

  override def userClass: Class[linalg.Vector] = classOf[linalg.Vector]

  override def equals(o: Any): Boolean = {
    o match {
      case v: VectorUDT => true
      case _ => false
    }
  }

  // see [SPARK-8647], this achieves the needed constant hash code without constant no.
  override def hashCode(): Int = classOf[VectorUDT].getName.hashCode()

  override def typeName: String = "vector"

  private[spark] override def asNullable: VectorUDT = this
}
