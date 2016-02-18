package com.truecar.mleap.core.feature

import com.truecar.mleap.core.linalg.Vector

import scala.collection.mutable

/**
  * Created by hwilkins on 12/30/15.
  *
  * Source adapted from: Apache Spark Utils and HashingTF, see NOTICE for contributors
  */
case class HashingTermFrequency(numFeatures: Int = 1 << 18) {
  def indexOf(term: Any): Int = nonNegativeMod(term.##, numFeatures)

  def apply(document: Iterable[_]): Vector = {
    val termFrequencies = mutable.HashMap.empty[Int, Double]
    document.foreach { term =>
      val i = indexOf(term)
      termFrequencies.put(i, termFrequencies.getOrElse(i, 0.0) + 1.0)
    }
    Vector.sparse(numFeatures, termFrequencies.toSeq)
  }

  /* Calculates 'x' modulo 'mod', takes to consideration sign of x,
 * i.e. if 'x' is negative, than 'x' % 'mod' is negative too
 * so function return (x % mod) + mod in that case.
 */
  def nonNegativeMod(x: Int, mod: Int): Int = {
    val rawMod = x % mod
    rawMod + (if (rawMod < 0) mod else 0)
  }
}
