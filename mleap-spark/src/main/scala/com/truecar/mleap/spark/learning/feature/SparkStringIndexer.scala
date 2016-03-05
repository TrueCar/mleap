package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.StringIndexerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.feature.StringIndexer

/**
  * Created by hwilkins on 12/3/15.
  */
object SparkStringIndexer extends EstimatorToSpark[StringIndexerEstimator] {
  override def toSpark(e: StringIndexerEstimator): StringIndexer = {
    new StringIndexer()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
      .setHandleInvalid(e.handleInvalid)
  }
}
