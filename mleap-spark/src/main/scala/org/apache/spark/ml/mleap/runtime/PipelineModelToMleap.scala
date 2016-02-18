package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.{PipelineModel, Transformer}

/**
  * Created by hwilkins on 12/18/15.
  */
case class PipelineModelToMleap(transformerToMleap: TransformerToMleap[Transformer])
  extends TransformerToMleap[PipelineModel] {
  override def toMleap(t: PipelineModel): transformer.PipelineModel = {
    transformer.PipelineModel(t.stages.map(transformerToMleap.toMleap))
  }
}
