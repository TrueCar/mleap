package com.truecar.mleap.runtime.estimator

/**
  * Created by hwilkins on 12/30/15.
  */
case class HashingTermFrequencyEstimator(name: String = Estimator.createName("hashingTermFrequency"),
                                         inputCol: String,
                                         outputCol: String,
                                         numFeatures: Int = 1 << 18) extends Estimator
