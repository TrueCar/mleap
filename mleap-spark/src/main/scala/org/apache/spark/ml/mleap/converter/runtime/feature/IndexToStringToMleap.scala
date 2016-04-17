package org.apache.spark.ml.mleap.converter.runtime.feature

import com.truecar.mleap.core.feature.ReverseStringIndexer
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.IndexToString
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hollinwilkins on 3/30/16.
  */
object IndexToStringToMleap extends TransformerToMleap[IndexToString, transformer.ReverseStringIndexerModel] {
  override def toMleap(t: IndexToString): transformer.ReverseStringIndexerModel = {
    transformer.ReverseStringIndexerModel(t.getInputCol,
      t.getOutputCol,
      ReverseStringIndexer(t.getLabels))
  }
}
