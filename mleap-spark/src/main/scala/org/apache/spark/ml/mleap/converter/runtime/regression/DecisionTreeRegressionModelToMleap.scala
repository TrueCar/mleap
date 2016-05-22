package org.apache.spark.ml.mleap.converter.runtime.regression

import com.truecar.mleap.core.regression
import com.truecar.mleap.core.regression.DecisionTreeRegression
import com.truecar.mleap.runtime.transformer
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap
import org.apache.spark.ml.regression.DecisionTreeRegressionModel

/**
  * Created by hwilkins on 11/18/15.
  */
object DecisionTreeRegressionModelToMleap extends TransformerToMleap[DecisionTreeRegressionModel, transformer.DecisionTreeRegressionModel] {
  override def toMleap(t: DecisionTreeRegressionModel): transformer.DecisionTreeRegressionModel = {
    transformer.DecisionTreeRegressionModel(featuresCol = t.getFeaturesCol,
      predictionCol = t.getPredictionCol,
      model = DecisionTreeRegression(t.rootNode.toMleap(false), t.numFeatures))
  }

  def toMleapCore(tree: DecisionTreeRegressionModel): regression.DecisionTreeRegression = {
    regression.DecisionTreeRegression(tree.rootNode.toMleap(false), tree.numFeatures)
  }
}
