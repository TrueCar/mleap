package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.classification.SupportVectorMachine
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.classification.SVMModel
import com.truecar.mleap.spark.MleapSparkSupport._

/**
  * Created by hollinwilkins on 4/14/16.
  */
object SupportVectorMachineToMleap extends TransformerToMleap[SVMModel] {
  override def toMleap(t: SVMModel): transformer.SupportVectorMachineModel = {
    val model = SupportVectorMachine(t.model.weights.toMleap, t.model.intercept)
    transformer.SupportVectorMachineModel(featuresCol = t.getFeaturesCol,
      predictionCol = t.getPredictionCol,
      model = model)
  }
}
