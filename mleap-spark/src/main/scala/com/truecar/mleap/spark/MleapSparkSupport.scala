package com.truecar.mleap.spark

import com.truecar.mleap.core.linalg
import com.truecar.mleap.learning.estimator
import com.truecar.mleap.runtime.transformer.{Transformer => MleapTransformer}
import com.truecar.mleap.learning.estimator.{Estimator => MleapEstimator}
import com.truecar.mleap.runtime.{Row => MleapRow, types}
import com.truecar.mleap.spark.learning.{MleapEstimatorWrapper, EstimatorToSpark, EstimatorToSparkSupport}
import com.truecar.mleap.spark.converter._
import org.apache.spark.ml.mleap.runtime.{DecisionTreeRegressionModelToMleap, TransformerToMleap, TransformerToMleapSupport}
import org.apache.spark.ml.regression.DecisionTreeRegressionModel
import org.apache.spark.ml.tree._
import org.apache.spark.ml.{PipelineStage, Transformer}
import org.apache.spark.mllib.linalg._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by hwilkins on 11/5/15.
  */
trait MleapSparkSupport extends TransformerToMleapSupport with EstimatorToSparkSupport {
  import scala.language.implicitConversions

  implicit def estimatorToSpark[E <: estimator.Estimator](e: E)
                                                         (implicit estimatorToSpark: EstimatorToSpark[E]): PipelineStage = {
    estimatorToSpark.toSpark(e)
  }
  implicit def transformerToMleap[T <: Transformer](transformer: T)
                                 (implicit transformerToMleap: TransformerToMleap[T]): MleapTransformer = {
    transformerToMleap.toMleap(transformer)
  }
  implicit def mleapTransformerWrapper[T <: MleapTransformer](t: T): MleapTransformerWrapper[T] = {
    MleapTransformerWrapper(t)
  }
  implicit def mleapEstimatorWrapper[E <: MleapEstimator](e: E)
                                                         (implicit estimatorToSpark: EstimatorToSpark[E]): MleapEstimatorWrapper[E] = {
    MleapEstimatorWrapper(e)
  }
  implicit def vectorToSpark(vector: linalg.Vector): VectorToSpark = VectorToSpark(vector)
  implicit def vectorToMleap(vector: Vector): VectorToMleap = VectorToMleap(vector)
  implicit def dataFrameToMleap(dataset: DataFrame): DataFrameToMleap = DataFrameToMleap(dataset)
  implicit def decisionTreeRegressionModelToMleap(tree: DecisionTreeRegressionModel): DecisionTreeRegressionModelToMleap = DecisionTreeRegressionModelToMleap(tree)
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
