package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 11/18/15.
  */
case class StringIndexerEstimator(name: String = Estimator.createName("stringIndexer"),
                                  inputCol: String,
                                  outputCol: String,
                                  handleInvalid: String = "error") extends Estimator
