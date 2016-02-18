package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.feature
import com.truecar.mleap.runtime.transformer.{HashingTermFrequencyModel, TokenizerModel, Transformer}
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.mleap.TransformerModel

/**
  * Created by hwilkins on 12/30/15.
  */
object TransformerModelToMleap extends TransformerToMleap[TransformerModel] {
  override def toMleap(t: TransformerModel): Transformer = t.transformer match {
    case transformer: Tokenizer => TokenizerModel(transformer.getInputCol,
      transformer.getOutputCol,
      feature.Tokenizer())
    case transformer: HashingTF => HashingTermFrequencyModel(transformer.getInputCol,
      transformer.getOutputCol,
      feature.HashingTermFrequency(transformer.getNumFeatures))
  }
}
