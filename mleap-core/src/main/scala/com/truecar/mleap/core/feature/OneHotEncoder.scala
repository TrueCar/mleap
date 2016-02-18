package com.truecar.mleap.core.feature

import com.truecar.mleap.core.linalg.Vector

/**
 * Created by hwilkins on 11/5/15.
 */
case class OneHotEncoder(size: Int) extends Serializable {
  val oneValue = Array(1.0)
  val emptyIndices = Array[Int]()
  val emptyValues = Array[Double]()

  def apply(label: Double): Vector = {
    val labelInt = label.toInt

    if(label != labelInt) {
      throw new Error("invalid label, must be integer")
    }

    if(label < size) {
      Vector.sparse(size, Array(labelInt), oneValue)
    } else {
      Vector.sparse(size, emptyIndices, emptyValues)
    }
  }
}
