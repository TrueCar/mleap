package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.feature.OneHotEncoder
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.OneHotEncoderModel

/**
  * Created by hwilkins on 12/18/15.
  */
object OneHotEncoderModelToMleap extends TransformerToMleap[OneHotEncoderModel] {
  override def toMleap(t: OneHotEncoderModel): transformer.OneHotEncoderModel = {
    transformer.OneHotEncoderModel(t.getInputCol,
      t.getOutputCol,
      OneHotEncoder(t.getSize))
  }
}
