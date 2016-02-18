package com.truecar.mleap.spark.converter

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.runtime.types.StructType
import com.truecar.mleap.spark.{SparkLeapFrame, MleapSparkSupport}
import org.apache.spark.sql.{types, Row, DataFrame, SQLContext}
import MleapSparkSupport._

/**
  * Created by hwilkins on 11/18/15.
  */
trait LeapFrameToSpark[T] {
  def toSpark(t: T)(implicit sqlContext: SQLContext): DataFrame
}

case class LeapFrameToSparkWrapper[T: LeapFrameToSpark](t: T) {
  def toSpark(implicit sqlContext: SQLContext): DataFrame = {
    implicitly[LeapFrameToSpark[T]].toSpark(t)
  }
}

object LeapFrameToSpark {
  implicit object SparkLeapFrameToSpark extends LeapFrameToSpark[SparkLeapFrame] {
    override def toSpark(t: SparkLeapFrame)
                        (implicit sqlContext: SQLContext): DataFrame = {
      val outputNames = t.schema.fields.map(_.name).toSet -- t.sparkSchema.fields.map(_.name).toSet
      val outputs = outputNames.map {
        name => (t.schema(name), t.schema.indexOf(name))
      }.toArray.sortBy(_._2)
      val (outputFields, outputIndices) = outputs.unzip
      val outputMleapSchema = StructTypeToSpark(StructType(outputFields)).toSpark
      val outputSchema = types.StructType(t.sparkSchema.fields ++ outputMleapSchema.fields)

      val rows = t.dataset.rdd.map {
        case (mleapRow, sparkValues) =>
          val mleapData = outputIndices.map {
            index =>
              mleapRow.get(index) match {
                case value: Vector => value.toSpark
                case value => value
              }
          }

          Row(sparkValues ++ mleapData: _*)
      }

      sqlContext.createDataFrame(rows, outputSchema)
    }
  }
}
