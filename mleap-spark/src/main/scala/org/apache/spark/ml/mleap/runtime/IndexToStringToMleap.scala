package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.feature.ReverseStringIndexer
import org.apache.spark.ml.feature.IndexToString
import com.truecar.mleap.runtime.transformer

/**
  * Created by hollinwilkins on 3/30/16.
  */
object IndexToStringToMleap extends TransformerToMleap[IndexToString] {
  override def toMleap(t: IndexToString): transformer.ReverseStringIndexerModel = {
    transformer.ReverseStringIndexerModel(t.getInputCol,
      t.getOutputCol,
      ReverseStringIndexer(t.getLabels))
  }
}
