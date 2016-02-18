package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.HashingTermFrequencyEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.HashingTF
import org.apache.spark.ml.mleap.{TransformerEstimator, TransformerModel}

/**
  * Created by hwilkins on 12/30/15.
  */
object SparkHashingTermFrequency extends EstimatorToSpark[HashingTermFrequencyEstimator] {
  override def toSpark(e: HashingTermFrequencyEstimator): Estimator[_] = {
    val model = TransformerModel(transformer = new HashingTF()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
      .setNumFeatures(e.numFeatures))
    TransformerEstimator(model = model)
  }
}
