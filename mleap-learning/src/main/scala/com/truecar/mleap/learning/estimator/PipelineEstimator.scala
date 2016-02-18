package com.truecar.mleap.learning.estimator

/**
  * Created by hwilkins on 11/18/15.
  */
case class PipelineEstimator(name: String = Estimator.createName("pipeline"),
                             estimators: Seq[Estimator] = Seq()) extends Estimator
