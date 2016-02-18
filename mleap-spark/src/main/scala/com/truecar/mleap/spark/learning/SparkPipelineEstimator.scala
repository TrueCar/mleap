package com.truecar.mleap.spark.learning

import com.truecar.mleap.learning.estimator.Estimator
import com.truecar.mleap.learning.estimator.PipelineEstimator
import org.apache.spark.ml
import org.apache.spark.ml.Pipeline

/**
  * Created by hwilkins on 12/3/15.
  */
case class SparkPipelineEstimator(estimator: EstimatorToSpark[Estimator]) extends EstimatorToSpark[PipelineEstimator] {
  override def toSpark(e: PipelineEstimator): ml.Estimator[_] = {
    val stages = e.estimators.map(estimator.toSpark)
    new Pipeline().setStages(stages.toArray)
  }
}
