package com.truecar.mleap.spark.learning.regression

import com.truecar.mleap.runtime.estimator.RandomForestClassificationEstimator
import com.truecar.mleap.spark.learning.EstimatorToSpark
import org.apache.spark.ml.classification.RandomForestClassifier

/**
  * Created by hwilkins on 1/26/16.
  */
object SparkRandomForestClassification extends EstimatorToSpark[RandomForestClassificationEstimator] {
  override def toSpark(e: RandomForestClassificationEstimator): RandomForestClassifier = {
    new RandomForestClassifier()
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
