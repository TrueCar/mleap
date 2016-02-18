package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.StandardScalerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.StandardScaler

/**
  * Created by hollinwilkins on 12/4/15.
  */
object SparkStandardScaler extends EstimatorToSpark[StandardScalerEstimator] {
  override def toSpark(e: StandardScalerEstimator): Estimator[_] = {
    new StandardScaler()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
      .setWithMean(e.withMean)
      .setWithStd(e.withStd)
  }
}
