package com.truecar.mleap.core.regression

import com.truecar.mleap.core.linalg.Vector

/**
 * Created by hwilkins on 11/5/15.
 */

case class LinearRegression(coefficients: Vector,
                            intercept: Double) extends Serializable {
  def apply(features: Vector): Double = {
    features.toBreeze.dot(coefficients.toBreeze) + intercept
  }
}
