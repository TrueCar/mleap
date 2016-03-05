package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.VectorAssembler

/**
  * Created by hwilkins on 12/18/15.
  */
object VectorAssemblerModelToMleap extends TransformerToMleap[VectorAssembler] {
  override def toMleap(t: VectorAssembler): transformer.VectorAssemblerModel = {
    transformer.VectorAssemblerModel(t.getInputCols,
      t.getOutputCol)
  }
}
