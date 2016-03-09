package com.truecar.mleap.core.classification

import com.truecar.mleap.core.linalg.{BLAS, Vector}


/**
  * Created by pahsan on 3/9/16.
  */
case class LogisticRegression(coefficients: Vector,
                              intercept: Double,
                              numClasses: Int = 2,
                              threshold: Double = 0.5) extends Serializable{

  val numFeatures = coefficients.size

  /** Computes the mean of the response variable given the predictors.
    * For binary classification only */
  private val margin: Vector => Double = (features) => {
    BLAS.dot(features, coefficients) + intercept
  }

  /**
    * Computes the logit function over the given predictors, assuming a
    * binomial distribution.
    * For binary classification only.
    */
  private val score: Vector => Double = (features) => {
    val m = margin(features)
    1.0 / (1.0 + math.exp(-m)) // Compute the logit function over the given predictors.
  }

  def apply(features: Vector): Double = {
    if (score(features) > threshold) 1 else 0
  }
}
