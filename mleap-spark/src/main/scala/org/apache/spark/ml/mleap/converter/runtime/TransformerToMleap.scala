package org.apache.spark.ml.mleap.converter.runtime

import org.apache.spark.ml.Transformer

/**
  * Created by hwilkins on 11/18/15.
  */
object TransformerToMleap {
  def apply[T, MT](t: T)
                  (implicit ttm: TransformerToMleap[T, MT]): MT = {
    ttm.toMleap(t)
  }

  def toMleap[T, MT](t: T)
                    (implicit ttm: TransformerToMleap[T, MT]): MT = {
    ttm.toMleap(t)
  }
}

trait TransformerToMleap[T, MT] {
  def toMleap(t: T): MT
  def toMleapLifted(t: Transformer): MT = {
    toMleap(t.asInstanceOf[T])
  }
}
