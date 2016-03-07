package com.truecar.mleap.spark.learning

import com.truecar.mleap.runtime.estimator.Estimator
import org.apache.spark.ml

/**
  * Created by hwilkins on 12/3/15.
  */
case class MultiEstimatorToSpark(map: Map[String, EstimatorToSpark[Estimator]]) extends EstimatorToSpark[Estimator] {
  override def toSpark(e: Estimator): ml.PipelineStage = {
    map(e.getClass.getCanonicalName).toSpark(e)
  }

  def withEstimatorToSpark(name: String, es: EstimatorToSpark[Estimator]): MultiEstimatorToSpark = {
    copy(map = map + (name -> es))
  }
}
