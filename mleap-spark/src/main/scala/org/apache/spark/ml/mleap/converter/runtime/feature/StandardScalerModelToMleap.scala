package org.apache.spark.ml.mleap.converter.runtime.feature

import com.truecar.mleap.core.feature.StandardScaler
import com.truecar.mleap.runtime.transformer
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.feature.StandardScalerModel
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hwilkins on 12/18/15.
  */
object StandardScalerModelToMleap extends TransformerToMleap[StandardScalerModel, transformer.StandardScalerModel] {
  override def toMleap(t: StandardScalerModel): transformer.StandardScalerModel = {
    val std = if(t.getWithStd) {
      Some(t.std.toMleap)
    } else {
      None
    }

    val mean = if(t.getWithMean) {
      Some(t.mean.toMleap)
    } else {
      None
    }

    transformer.StandardScalerModel(uid = t.uid,
      inputCol = t.getInputCol,
      outputCol = t.getOutputCol,
      scaler = StandardScaler(std, mean))
  }
}
