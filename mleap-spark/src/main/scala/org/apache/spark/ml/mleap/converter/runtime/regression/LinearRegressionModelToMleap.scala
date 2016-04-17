package org.apache.spark.ml.mleap.converter.runtime.regression

import com.truecar.mleap.core.regression.LinearRegression
import com.truecar.mleap.runtime.transformer
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap
import org.apache.spark.ml.regression.LinearRegressionModel

/**
  * Created by hwilkins on 12/18/15.
  */
object LinearRegressionModelToMleap extends TransformerToMleap[LinearRegressionModel, transformer.LinearRegressionModel] {
  override def toMleap(t: LinearRegressionModel): transformer.LinearRegressionModel = {
    transformer.LinearRegressionModel(t.getFeaturesCol,
      t.getPredictionCol,
      LinearRegression(t.coefficients.toMleap,
        t.intercept))
  }
}
