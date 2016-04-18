package com.truecar.mleap.core.regression

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.core.tree.{DecisionTree, Node}

/**
 * Created by hwilkins on 11/8/15.
 */
case class DecisionTreeRegression(rootNode: Node, numFeatures: Int) extends DecisionTree {
  def predict(features: Vector): Double = {
    rootNode.predictImpl(features).prediction
  }
}
