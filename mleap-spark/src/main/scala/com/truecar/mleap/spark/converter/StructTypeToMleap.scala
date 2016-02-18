package com.truecar.mleap.spark.converter

import com.truecar.mleap.runtime.types
import org.apache.spark.SparkException
import org.apache.spark.mllib.linalg.VectorUDT
import org.apache.spark.sql.types._

/**
  * Created by hwilkins on 11/18/15.
  */
case class StructTypeToMleap(schema: StructType) {
  def toMleap: types.StructType = {
    val leapFields = schema.fields.map {
      field =>
        val sparkType = field.dataType
        val sparkTypeName = sparkType.typeName
        val dataType = sparkType match {
          case _: NumericType | BooleanType => types.DoubleType
          case _: StringType => types.StringType
          case _: VectorUDT => types.VectorType
          case dataType: ArrayType if dataType.elementType == StringType => types.StringArrayType
          case _ => throw new SparkException(s"unsupported MLeap datatype: $sparkTypeName")
        }

        types.StructField(field.name, dataType)
    }
    types.StructType(leapFields)
  }
}
