package com.truecar.mleap.core.classification

import com.truecar.mleap.core.linalg.{BLAS, Vector}

/**
  * Created by hollinwilkins on 4/14/16.
  */
case class SupportVectorMachine(coefficients: Vector,
                                intercept: Double,
                                threshold: Option[Double] = None) {
  def apply(features: Vector): Double = {
    val margin = BLAS.dot(coefficients, features) + intercept

    threshold match {
      case Some(t) => if (margin > t) 1.0 else 0.0
      case None => margin
    }
  }
}
