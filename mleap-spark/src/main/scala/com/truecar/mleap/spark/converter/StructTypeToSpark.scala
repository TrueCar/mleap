package com.truecar.mleap.spark.converter

import com.truecar.mleap.runtime.types
import org.apache.spark.mllib.linalg.VectorUDT
import org.apache.spark.sql.types._

/**
  * Created by hwilkins on 11/18/15.
  */
case class StructTypeToSpark(schema: types.StructType) {
  def toSpark: StructType = {
    val fields = schema.fields.map {
      field =>
        field.dataType match {
          case types.DoubleType => StructField(field.name, DoubleType)
          case types.StringType => StructField(field.name, StringType)
          case types.VectorType => StructField(field.name, new VectorUDT())
          case types.StringArrayType => StructField(field.name, new ArrayType(StringType, containsNull = false))
        }
    }

    StructType(fields)
  }
}
