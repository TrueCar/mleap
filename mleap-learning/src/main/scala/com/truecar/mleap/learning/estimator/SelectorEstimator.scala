package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 12/3/15.
  */
case class SelectorEstimator(name: String = Estimator.createName("selector"),
                             fieldNames: Seq[String]) extends Estimator
