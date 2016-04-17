package com.truecar.mleap.spark

import com.truecar.mleap.core.linalg
import com.truecar.mleap.runtime.transformer.{Transformer => MleapTransformer}
import com.truecar.mleap.runtime.{types, Row => MleapRow}
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.mleap.converter._
import org.apache.spark.ml.mleap.converter.runtime.{BaseTransformerConverter, TransformerToMleap}
import org.apache.spark.ml.mleap.converter.runtime.classification.DecisionTreeClassificationModelToMleap
import org.apache.spark.ml.mleap.converter.runtime.regression.DecisionTreeRegressionModelToMleap
import org.apache.spark.ml.regression.DecisionTreeRegressionModel
import org.apache.spark.ml.tree._
import org.apache.spark.ml.Transformer
import org.apache.spark.mllib.linalg._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by hwilkins on 11/5/15.
  */
trait MleapSparkSupport extends BaseTransformerConverter {
  import scala.language.implicitConversions

  implicit def transformerToMleapLifted[T <: Transformer]
  (t: T)
  (implicit transformerToMleap: TransformerToMleap[T, _ <: MleapTransformer]): MleapTransformer = {
    transformerToMleap.toMleapLifted(t)
  }

  implicit def mleapTransformerWrapper[T <: MleapTransformer](t: T): MleapTransformerWrapper[T] = {
    MleapTransformerWrapper(t)
  }

  implicit def vectorToSpark(vector: linalg.Vector): VectorToSpark = VectorToSpark(vector)
  implicit def vectorToMleap(vector: Vector): VectorToMleap = VectorToMleap(vector)
  implicit def dataFrameToMleap(dataset: DataFrame): DataFrameToMleap = DataFrameToMleap(dataset)
  implicit def decisionTreeRegressionModelToMleap(tree: DecisionTreeRegressionModel): DecisionTreeRegressionModelToMleap = DecisionTreeRegressionModelToMleap(tree)
  implicit def decisionTreeClassificationModelToMleap(tree: DecisionTreeClassificationModel): DecisionTreeClassificationModelToMleap = DecisionTreeClassificationModelToMleap(tree)
  implicit def nodeToMleap(node: Node): NodeToMleap = NodeToMleap(node)
  implicit def splitToMleap(split: Split): SplitToMleap = SplitToMleap(split)
  implicit def structTypeToMleap(schema: StructType): StructTypeToMleap = StructTypeToMleap(schema)

  implicit def rowToSpark(row: MleapRow): RowToSpark = RowToSpark(row)
  implicit def structTypeToSpark(schema: types.StructType): StructTypeToSpark = StructTypeToSpark(schema)
  implicit def leapFrameToSpark[T: LeapFrameToSpark](frame: T): LeapFrameToSparkWrapper[T] = {
    LeapFrameToSparkWrapper(frame)
  }
  implicit def leapFrameToSparkConvert[T: LeapFrameToSpark](frame: T)
                                                           (implicit sqlContext: SQLContext): DataFrame = {
    implicitly[LeapFrameToSpark[T]].toSpark(frame)
  }
  implicit def dataFrameToLeapFrame(dataFrame: DataFrame): SparkLeapFrame = dataFrame.toMleap
}
object MleapSparkSupport extends MleapSparkSupport
