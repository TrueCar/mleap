package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.spark.MleapSparkSupport._
import com.truecar.mleap.core.regression.RandomForestRegression
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.regression.{DecisionTreeRegressionModel, RandomForestRegressionModel}

/**
  * Created by hwilkins on 12/18/15.
  */
object RandomForestRegressionModelToMleap extends TransformerToMleap[RandomForestRegressionModel] {
  override def toMleap(t: RandomForestRegressionModel): transformer.RandomForestRegressionModel = {
    val model = RandomForestRegression(t.trees.asInstanceOf[Array[DecisionTreeRegressionModel]].map(_.toMleap), t.treeWeights)

    transformer.RandomForestRegressionModel(t.getFeaturesCol,
      t.getPredictionCol,
      model)
  }
}
