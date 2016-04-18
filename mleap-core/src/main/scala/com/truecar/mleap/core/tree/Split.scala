package com.truecar.mleap.core.tree

import com.truecar.mleap.core.linalg.Vector

/**
  * Created by hwilkins on 11/8/15.
  */
object Split {
  val categoricalSplitName = "CategoricalSplit"
  val continuousSplitName = "ContinuousSplit"
}

sealed trait Split extends Serializable {
  def featureIndex: Int

  def shouldGoLeft(features: Vector): Boolean
  def shouldGoLeft(binnedFeature: Int, splits: Array[Split]): Boolean
}

final case class CategoricalSplit(featureIndex: Int,
                                  numCategories: Int,
                                  categories: Array[Double],
                                  isLeft: Boolean) extends Split {
  override def shouldGoLeft(features: Vector): Boolean = {
    if(isLeft) {
      categories.contains(features(featureIndex))
    } else {
      !categories.contains(features(featureIndex))
    }
  }

  override def shouldGoLeft(binnedFeature: Int, splits: Array[Split]): Boolean = {
    if(isLeft) {
      categories.contains(binnedFeature.toDouble)
    } else {
      !categories.contains(binnedFeature.toDouble)
    }
  }
}

final case class ContinuousSplit(featureIndex: Int,
                                 threshold: Double) extends Split {
  override def shouldGoLeft(features: Vector): Boolean = features(featureIndex) <= threshold

  override def shouldGoLeft(binnedFeature: Int, splits: Array[Split]): Boolean = {
    if(binnedFeature == splits.length) {
      false
    } else {
      val featureUpperBound = splits(binnedFeature).asInstanceOf[ContinuousSplit].threshold
      featureUpperBound <= threshold
    }
  }
}
