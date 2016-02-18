package com.truecar.mleap.spark.converter

import com.truecar.mleap.core.tree
import org.apache.spark.ml.tree.{ContinuousSplit, CategoricalSplit, Split}

/**
  * Created by hwilkins on 11/18/15.
  */
case class SplitToMleap(split: Split) {
  def toMleap: tree.Split = {
    split match {
      case split: CategoricalSplit =>
        val (isLeft, categories) = if(split.leftCategories.length >= split.rightCategories.length) {
          (true, split.leftCategories)
        } else {
          (false, split.rightCategories)
        }
        tree.CategoricalSplit(split.featureIndex, categories, isLeft)
      case split: ContinuousSplit =>
        tree.ContinuousSplit(split.featureIndex, split.threshold)
    }
  }
}
