package com.truecar.mleap.spark.learning.feature

import com.truecar.mleap.learning.estimator.VectorAssemblerEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.mleap.VectorAssembler

/**
  * Created by hwilkins on 12/3/15.
  */
object SparkVectorAssembler extends EstimatorToSpark[VectorAssemblerEstimator] {
  override def toSpark(e: VectorAssemblerEstimator): Estimator[_] = {
    new VectorAssembler()
      .setInputCols(e.inputCols.toArray)
      .setOutputCol(e.outputCol)
  }
}
