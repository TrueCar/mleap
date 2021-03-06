package org.apache.spark.ml.mleap.converter

import com.truecar.mleap.runtime.transformer.Transformer
import org.apache.spark.sql.DataFrame

/**
  * Created by hwilkins on 11/18/15.
  */
case class MleapTransformerWrapper[T <: Transformer](transformer: T) {
  def sparkTransform(dataset: DataFrame): DataFrame = {
    val frame = DataFrameToMleap(dataset).toMleap
    LeapFrameToSpark.SparkLeapFrameToSpark.toSpark(transformer.transform(frame).get)(dataset.sqlContext)
  }
}
