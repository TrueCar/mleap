package org.apache.spark.ml.mleap.converter.runtime.classification

import com.truecar.mleap.core.classification.SupportVectorMachine
import com.truecar.mleap.runtime.transformer
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.mleap.classification.SVMModel
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hollinwilkins on 4/14/16.
  */
object SupportVectorMachineModelToMleap extends TransformerToMleap[SVMModel, transformer.SupportVectorMachineModel] {
  override def toMleap(t: SVMModel): transformer.SupportVectorMachineModel = {
    val model = SupportVectorMachine(t.model.weights.toMleap, t.model.intercept)
    transformer.SupportVectorMachineModel(uid = t.uid,
      featuresCol = t.getFeaturesCol,
      predictionCol = t.getPredictionCol,
      model = model)
  }
}
