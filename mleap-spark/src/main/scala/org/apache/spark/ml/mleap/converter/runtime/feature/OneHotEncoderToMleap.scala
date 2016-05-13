package org.apache.spark.ml.mleap.converter.runtime.feature

import com.truecar.mleap.core.feature.OneHotEncoder
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap
import org.apache.spark.ml.mleap.feature.OneHotEncoderModel

/**
  * Created by hwilkins on 12/18/15.
  */
object OneHotEncoderToMleap extends TransformerToMleap[OneHotEncoderModel, transformer.OneHotEncoderModel] {
  override def toMleap(t: OneHotEncoderModel): transformer.OneHotEncoderModel = {
    val model = OneHotEncoder(t.size)
    transformer.OneHotEncoderModel(inputCol = t.getInputCol,
      outputCol = t.getOutputCol,
      model = model)
  }
}
