package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 11/18/15.
  */
case class OneHotEncoderEstimator(name: String = Estimator.createName("oneHotEncoder"),
                                  inputCol: String,
                                  outputCol: String,
                                  dropLast: Boolean = true) extends Estimator
