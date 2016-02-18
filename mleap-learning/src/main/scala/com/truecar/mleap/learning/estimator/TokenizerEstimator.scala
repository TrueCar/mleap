package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 12/30/15.
  */
case class TokenizerEstimator(name: String = Estimator.createName("tokenizer"),
                             inputCol: String,
                             outputCol: String) extends Estimator
