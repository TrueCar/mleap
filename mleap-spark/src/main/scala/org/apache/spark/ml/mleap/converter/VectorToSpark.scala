package org.apache.spark.ml.mleap.converter

import com.truecar.mleap.core.linalg
import org.apache.spark.mllib.linalg.{Vectors, Vector}

/**
  * Created by hwilkins on 11/18/15.
  */
case class VectorToSpark(vector: linalg.Vector) {
  def toSpark: Vector = vector match {
    case linalg.DenseVector(values) => Vectors.dense(values)
    case linalg.SparseVector(size, indices, values) => Vectors.sparse(size, indices, values)
  }
}