package com.truecar.mleap.spark.learning

import com.truecar.mleap.learning.estimator.Estimator
import com.truecar.mleap.runtime.transformer.Transformer
import org.apache.spark.ml
import org.apache.spark.ml.mleap.runtime.TransformerToMleap
import org.apache.spark.sql.DataFrame

/**
  * Created by hwilkins on 12/28/15.
  */
case class MleapEstimatorWrapper[E <: Estimator](estimator: E)
                                                (implicit estimatorToSpark: EstimatorToSpark[E]) {
  def sparkEstimate(dataset: DataFrame): ml.Transformer = {
    val sparkEstimator: ml.PipelineStage = estimatorToSpark.toSpark(estimator)
    sparkEstimator match {
      case estimator: ml.Estimator[_] => estimator.fit(dataset).asInstanceOf[ml.Transformer]
      case transformer: ml.Transformer => transformer
    }
  }

  def estimate(dataset: DataFrame)
              (implicit transformerToMleap: TransformerToMleap[ml.Transformer]): Transformer = {
    transformerToMleap.toMleap(sparkEstimate(dataset))
  }
}
