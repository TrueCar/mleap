package com.truecar.mleap.core.classification

import com.truecar.mleap.core.linalg.{DenseVector, Vector}

/**
  * Created by hollinwilkins on 3/30/16.
  */
object ProbabilisticClassification {
  def rawToPrediction(raw: Vector): Double = raw.argmax

  def rawToProbability(raw: Vector): Vector = raw match {
    case raw: DenseVector =>
      val v = raw.copy()
      val sum = v.values.sum
      if (sum != 0) {
        var i = 0
        val size = v.size
        while (i < size) {
          v.values(i) /= sum
          i += 1
        }
      }
      v
    case _ => throw new Error(s"Unsupported Vector type: ${raw.getClass.getCanonicalName}")
  }
}
