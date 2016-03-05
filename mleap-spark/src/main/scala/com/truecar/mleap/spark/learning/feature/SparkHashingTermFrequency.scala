package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.HashingTermFrequencyEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.feature.HashingTF

/**
  * Created by hwilkins on 12/30/15.
  */
object SparkHashingTermFrequency extends EstimatorToSpark[HashingTermFrequencyEstimator] {
  override def toSpark(e: HashingTermFrequencyEstimator): HashingTF = {
    new HashingTF()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
      .setNumFeatures(e.numFeatures)
  }
}
