package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.runtime.estimator.TokenizerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.feature.Tokenizer

/**
  * Created by hwilkins on 12/30/15.
  */
object SparkTokenizer extends EstimatorToSpark[TokenizerEstimator] {
  override def toSpark(e: TokenizerEstimator): Tokenizer = {
    new Tokenizer()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol)
  }
}
