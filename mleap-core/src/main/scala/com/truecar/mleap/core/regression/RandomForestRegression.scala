package com.truecar.mleap.core.regression

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.core.tree.TreeEnsemble

/**
 * Created by hwilkins on 11/8/15.
 */
case class RandomForestRegression(trees: Seq[DecisionTreeRegression], numFeatures: Int) extends TreeEnsemble {
  val numTrees = trees.length

  def apply(features: Vector): Double = predict(features)

  def predict(features: Vector): Double = {
    trees.map(_.predict(features)).sum / numTrees
  }

  override val treeWeights: Seq[Double] = Array.fill[Double](numTrees)(1.0)
}
