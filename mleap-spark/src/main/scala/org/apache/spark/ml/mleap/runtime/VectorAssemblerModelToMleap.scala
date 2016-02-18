package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.spark.MleapSparkSupport._
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.VectorAssemblerModel

/**
  * Created by hwilkins on 12/18/15.
  */
object VectorAssemblerModelToMleap extends TransformerToMleap[VectorAssemblerModel] {
  override def toMleap(t: VectorAssemblerModel): transformer.VectorAssemblerModel = {
    transformer.VectorAssemblerModel(t.getInputSchema.toMleap,
      t.getOutputCol)
  }
}
