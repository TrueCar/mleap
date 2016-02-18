package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.spark.MleapSparkSupport._
import com.truecar.mleap.core.feature.StandardScaler
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.StandardScalerModel

/**
  * Created by hwilkins on 12/18/15.
  */
object StandardScalerModelToMleap extends TransformerToMleap[StandardScalerModel] {
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

    transformer.StandardScalerModel(t.getInputCol,
      t.getOutputCol,
      StandardScaler(std, mean))
  }
}
