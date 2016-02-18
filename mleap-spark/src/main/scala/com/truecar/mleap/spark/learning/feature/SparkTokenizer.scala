package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.TokenizerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.ml.mleap.{TransformerModel, TransformerEstimator}

/**
  * Created by hwilkins on 12/30/15.
  */
object SparkTokenizer extends EstimatorToSpark[TokenizerEstimator] {
  override def toSpark(e: TokenizerEstimator): Estimator[_] = {
    val model = TransformerModel(transformer = new Tokenizer()
      .setInputCol(e.inputCol)
      .setOutputCol(e.outputCol))
    TransformerEstimator(model = model)
  }
}
