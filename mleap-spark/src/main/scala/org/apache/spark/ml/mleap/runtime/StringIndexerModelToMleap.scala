package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.feature.StringIndexer
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.mleap.StringIndexerModel

/**
  * Created by hwilkins on 12/18/15.
  */
object StringIndexerModelToMleap extends TransformerToMleap[StringIndexerModel] {
  override def toMleap(t: StringIndexerModel): transformer.StringIndexerModel = {
    transformer.StringIndexerModel(t.getInputCol,
      t.getOutputCol,
      StringIndexer(t.getLabels))
  }
}
