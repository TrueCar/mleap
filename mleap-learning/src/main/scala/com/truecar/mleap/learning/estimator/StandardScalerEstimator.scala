package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 11/19/15.
  */
case class StandardScalerEstimator(name: String = Estimator.createName("standardScaler"),
                                   inputCol: String,
                                   outputCol: String,
                                   withMean: Boolean = false,
                                   withStd: Boolean = true) extends Estimator
