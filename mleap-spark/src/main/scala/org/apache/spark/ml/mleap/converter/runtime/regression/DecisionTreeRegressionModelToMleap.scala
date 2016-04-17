package org.apache.spark.ml.mleap.converter.runtime.regression

import com.truecar.mleap.core.regression
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.regression.DecisionTreeRegressionModel

/**
  * Created by hwilkins on 11/18/15.
  */
case class DecisionTreeRegressionModelToMleap(tree: DecisionTreeRegressionModel) {
  def toMleap: regression.DecisionTreeRegression = {
    regression.DecisionTreeRegression(tree.rootNode.toMleap(false), tree.numFeatures)
  }
}
