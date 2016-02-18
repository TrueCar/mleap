package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 11/19/15.
  */
case class RandomForestRegressionEstimator(name: String = Estimator.createName("randomForestRegression"),
                                           cacheNodeIds: Boolean = false,
                                           checkpointInterval: Int = 10,
                                           featureSubsetStrategy: String = "auto",
                                           impurity: String = "variance",
                                           maxBins: Int = 32,
                                           maxDepth: Int = 5,
                                           maxMemoryInMB: Int = 256,
                                           minInfoGain: Double = 0.0,
                                           minInstancesPerNode: Int = 1,
                                           numTrees: Int = 20,
                                           seed: Long = classOf[RandomForestRegressionEstimator].getName.hashCode.toLong,
                                           subsamplingRate: Double = 1.0,
                                           featuresCol: String = "features",
                                           labelCol: String = "label",
                                           predictionCol: String = "prediction") extends Estimator
