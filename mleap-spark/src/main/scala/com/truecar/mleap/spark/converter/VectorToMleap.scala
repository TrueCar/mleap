package com.truecar.mleap.spark.converter

import com.truecar.mleap.core.linalg
import org.apache.spark.mllib.linalg.{SparseVector, DenseVector, Vector}

/**
  * Created by hwilkins on 11/18/15.
  */
case class VectorToMleap(vector: Vector) {
  def toMleap: linalg.Vector = {
    vector match {
      case DenseVector(values) => linalg.Vector.dense(values)
      case SparseVector(size, indices, values) => linalg.Vector.sparse(size, indices, values)
    }
  }
}
