package org.apache.spark.ml.mleap.converter.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.{PipelineModel, Transformer}

/**
  * Created by hwilkins on 12/18/15.
  */
case class PipelineModelToMleap(converter: SparkTransformerConverter)
  extends TransformerToMleap[PipelineModel, transformer.PipelineModel] {
  override def toMleap(t: PipelineModel): transformer.PipelineModel = {
    transformer.PipelineModel(uid = t.uid,
      transformers = t.stages.map(converter.convert))
  }
}
