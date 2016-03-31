package com.truecar.mleap.core.classification

import com.truecar.mleap.core.linalg.{DenseVector, Vector}
import com.truecar.mleap.core.tree.Node

/**
  * Created by hollinwilkins on 3/30/16.
  */
case class DecisionTreeClassification(rootNode: Node,
                                      numFeatures: Int,
                                      numClasses: Int) {
  def apply(features: Vector): Double = {
    ProbabilisticClassification.rawToPrediction(predictRaw(features))
  }

  def predictRaw(features: Vector): Vector = {
    rootNode.predictImpl(features).impurityStats.get
  }
}
