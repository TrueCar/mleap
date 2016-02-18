package com.truecar.mleap.spark.learning

import com.truecar.mleap.learning.estimator.Estimator
import org.apache.spark.ml

/**
  * Created by hwilkins on 12/3/15.
  */
case class MultiEstimatorToSpark(map: Map[String, EstimatorToSpark[Estimator]]) extends EstimatorToSpark[Estimator] {
  override def toSpark(e: Estimator): ml.Estimator[_] = {
    map(e.typeName).toSpark(e)
  }

  def withEstimatorToSpark(name: String, es: EstimatorToSpark[Estimator]): MultiEstimatorToSpark = {
    copy(map = map + (name -> es))
  }
}
