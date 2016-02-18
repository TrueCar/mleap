package com.truecar.mleap.spark.learning.regression

import com.truecar.mleap.learning.estimator.RandomForestRegressionEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.regression.RandomForestRegressor

/**
  * Created by hwilkins on 1/26/16.
  */
object SparkRandomForestRegression extends EstimatorToSpark[RandomForestRegressionEstimator] {
  override def toSpark(e: RandomForestRegressionEstimator): Estimator[_] = {
    new RandomForestRegressor()
        .setCacheNodeIds(e.cacheNodeIds)
        .setCheckpointInterval(e.checkpointInterval)
      .setFeatureSubsetStrategy(e.featureSubsetStrategy)
      .setImpurity(e.impurity)
      .setMaxBins(e.maxBins)
      .setMaxDepth(e.maxDepth)
      .setMaxMemoryInMB(e.maxMemoryInMB)
      .setMinInfoGain(e.minInfoGain)
      .setMinInstancesPerNode(e.minInstancesPerNode)
      .setNumTrees(e.numTrees)
      .setSeed(e.seed)
      .setSubsamplingRate(e.subsamplingRate)
      .setFeaturesCol(e.featuresCol)
      .setLabelCol(e.labelCol)
      .setPredictionCol(e.predictionCol)
  }
}
