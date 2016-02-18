package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.OneHotEncoderEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.{mleap, Estimator}

/**
  * Created by hwilkins on 12/3/15.
  */
object SparkOneHotEncoder extends EstimatorToSpark[OneHotEncoderEstimator] {
  override def toSpark(e: OneHotEncoderEstimator): Estimator[_] = {
    new mleap.OneHotEncoder()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
      .setDropLast(e.dropLast)
  }
}
