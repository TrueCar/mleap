package org.apache.spark.ml.mleap.converter.runtime.feature

import com.truecar.mleap.core.feature.StringIndexer
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.StringIndexerModel
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hwilkins on 12/18/15.
  */
object StringIndexerModelToMleap extends TransformerToMleap[StringIndexerModel, transformer.StringIndexerModel] {
  override def toMleap(t: StringIndexerModel): transformer.StringIndexerModel = {
    transformer.StringIndexerModel(uid = t.uid,
      inputCol = t.getInputCol,
      outputCol = t.getOutputCol,
      indexer = StringIndexer(t.labels))
  }
}
