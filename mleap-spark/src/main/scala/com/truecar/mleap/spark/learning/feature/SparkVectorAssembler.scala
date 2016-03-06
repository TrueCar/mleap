package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.runtime.estimator.VectorAssemblerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.feature.VectorAssembler

/**
  * Created by hwilkins on 12/3/15.
  */
object SparkVectorAssembler extends EstimatorToSpark[VectorAssemblerEstimator] {
  override def toSpark(e: VectorAssemblerEstimator): VectorAssembler = {
    new VectorAssembler()
      .setInputCols(e.inputCols.toArray)
      .setOutputCol(e.outputCol)
  }
}
