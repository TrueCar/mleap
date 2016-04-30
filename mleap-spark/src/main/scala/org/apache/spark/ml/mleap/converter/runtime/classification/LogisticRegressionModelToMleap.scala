package org.apache.spark.ml.mleap.converter.runtime.classification

import com.truecar.mleap.core.classification.LogisticRegression
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap
import com.truecar.mleap.spark.MleapSparkSupport._

/**
  * Created by hwilkins on 12/18/15.
  */
object LogisticRegressionModelToMleap extends TransformerToMleap[LogisticRegressionModel, transformer.LogisticRegressionModel] {
  override def toMleap(t: LogisticRegressionModel): transformer.LogisticRegressionModel = {
    val model = LogisticRegression(t.coefficients.toMleap, t.intercept)
    transformer.LogisticRegressionModel(featuresCol = t.getFeaturesCol,
      predictionCol = t.getPredictionCol,
      model = model)
  }
}
