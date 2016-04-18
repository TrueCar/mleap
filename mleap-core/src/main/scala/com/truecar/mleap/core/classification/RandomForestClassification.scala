package com.truecar.mleap.core.classification

import com.truecar.mleap.core.linalg.Vector

/**
  * Created by hollinwilkins on 3/30/16.
  */
case class RandomForestClassification(trees: Seq[DecisionTreeClassification],
                                      numFeatures: Int,
                                      numClasses: Int) {
  def apply(features: Vector): Double = {
    ProbabilisticClassification.rawToPrediction(predictRaw(features))
  }

  def predictRaw(raw: Vector): Vector = {
    val votes = Array.fill[Double](numClasses)(0.0)
    trees.view.foreach { tree =>
      val classCounts: Array[Double] = tree.rootNode.predictImpl(raw).impurityStats.get.toArray
      val total = classCounts.sum
      if (total != 0) {
        var i = 0
        while (i < numClasses) {
          votes(i) += classCounts(i) / total
          i += 1
        }
      }
    }
    Vector.dense(votes)
  }
}
