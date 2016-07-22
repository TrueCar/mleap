package org.apache.spark.ml.mleap.converter.runtime.feature

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hwilkins on 12/18/15.
  */
object VectorAssemblerModelToMleap extends TransformerToMleap[VectorAssembler, transformer.VectorAssemblerModel] {
  override def toMleap(t: VectorAssembler): transformer.VectorAssemblerModel = {
    transformer.VectorAssemblerModel(uid = t.uid,
      inputCols = t.getInputCols,
      outputCol = t.getOutputCol)
  }
}
