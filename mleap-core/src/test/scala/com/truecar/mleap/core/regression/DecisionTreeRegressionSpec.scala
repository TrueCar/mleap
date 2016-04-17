package com.truecar.mleap.core.regression

import com.truecar.mleap.core.tree.{ContinuousSplit, InternalNode, LeafNode}
import com.truecar.mleap.core.linalg
import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/21/16.
  */
class DecisionTreeRegressionSpec extends FunSpec {
  describe("#predict") {
    it("returns the prediction for the decision tree") {
      val leftNode = LeafNode(.78, .33)
      val rightNode = LeafNode(.34, 6.7)
      val split = ContinuousSplit(0, .5)
      val node = InternalNode(.77, 6.7, 3.4, leftNode, rightNode, split)
      val features = linalg.Vector.dense(Array(0.3, 1.0, 43.23, -21.2, 66.7))
      val regression = DecisionTreeRegression(node, 5)

      assert(regression.predict(features) == .78)
    }
  }
}
