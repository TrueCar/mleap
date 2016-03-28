package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.regression
import com.truecar.mleap.core.regression.DecisionTreeRegression
import com.truecar.mleap.spark.MleapSparkSupport
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.regression.DecisionTreeRegressionModel

/**
  * Created by hwilkins on 11/18/15.
  */
case class DecisionTreeRegressionModelToMleap(tree: DecisionTreeRegressionModel) {
  def toMleap: DecisionTreeRegression = {
    regression.DecisionTreeRegression(tree.rootNode.toMleap, tree.numFeatures)
  }
}
