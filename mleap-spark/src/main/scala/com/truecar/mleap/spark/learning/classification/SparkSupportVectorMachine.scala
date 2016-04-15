package com.truecar.mleap.spark.learning.classification

import com.truecar.mleap.runtime.estimator.SupportVectorMachineEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.mleap.classification.SVM

/**
  * Created by hollinwilkins on 4/14/16.
  */
object SparkSupportVectorMachine extends EstimatorToSpark[SupportVectorMachineEstimator] {
  override def toSpark(e: SupportVectorMachineEstimator): SVM = {
    new SVM().setStepSize(e.stepSize)
      .setNumIterations(e.numIterations)
      .setFitIntercept(e.fitIntercept)
      .setRegParam(e.regParam)
      .setMiniBatchFraction(e.miniBatchFraction)
      .setThreshold(e.threshold)
      .setFeaturesCol(e.featuresCol)
      .setLabelCol(e.labelCol)
      .setPredictionCol(e.predictionCol)
  }
}
