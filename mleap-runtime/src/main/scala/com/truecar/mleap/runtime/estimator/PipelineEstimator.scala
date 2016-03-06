package com.truecar.mleap.runtime.estimator

/**
  * Created by hwilkins on 11/18/15.
  */
case class PipelineEstimator(name: String = Estimator.createName("pipeline"),
                             estimators: Seq[Estimator] = Seq()) extends Estimator
