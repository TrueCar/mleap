package org.apache.spark.ml.mleap.converter.runtime.regression

import com.truecar.mleap.core.regression.RandomForestRegression
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap
import org.apache.spark.ml.regression.{DecisionTreeRegressionModel, RandomForestRegressionModel}

/**
  * Created by hwilkins on 12/18/15.
  */
object RandomForestRegressionModelToMleap extends TransformerToMleap[RandomForestRegressionModel, transformer.RandomForestRegressionModel] {
  override def toMleap(t: RandomForestRegressionModel): transformer.RandomForestRegressionModel = {
    val trees = t.trees.asInstanceOf[Array[DecisionTreeRegressionModel]].map(tree => DecisionTreeRegressionModelToMleap(tree).toMleap)
    val model = RandomForestRegression(trees, t.numFeatures)

    transformer.RandomForestRegressionModel(t.getFeaturesCol,
      t.getPredictionCol,
      model)
  }
}
