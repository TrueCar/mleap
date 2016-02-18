package com.truecar.mleap.core.tree

/**
 * Created by hwilkins on 11/8/15.
 */
trait TreeEnsemble extends Serializable {
  def trees: Seq[DecisionTree]
  def treeWeights: Seq[Double]
}
