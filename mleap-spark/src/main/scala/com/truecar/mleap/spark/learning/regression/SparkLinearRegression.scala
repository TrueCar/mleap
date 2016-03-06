package com.truecar.mleap.spark.learning.regression

import com.truecar.mleap.runtime.estimator.LinearRegressionEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.regression.LinearRegression

object SparkLinearRegression extends EstimatorToSpark[LinearRegressionEstimator] {
  override def toSpark(e: LinearRegressionEstimator): LinearRegression = {
    new LinearRegression()
      .setElasticNetParam(e.elasticNetParam)
      .setFitIntercept(e.fitIntercept)
      .setMaxIter(e.maxIter)
      .setRegParam(e.regParam)
      .setStandardization(e.standardization)
      .setTol(e.tol)
      .setFeaturesCol(e.featuresCol)
      .setLabelCol(e.labelCol)
      .setPredictionCol(e.predictionCol)
  }
}
