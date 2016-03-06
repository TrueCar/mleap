package com.truecar.mleap.serialization.json

import com.truecar.mleap.runtime.json.feature.StringIndexerModelSerializer
import com.truecar.mleap.runtime.transformer.PipelineModelSerializer
import com.truecar.mleap.serialization.MleapSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object DefaultJsonMleapSerializer {
  def createSerializer(): MleapSerializer = {
    val serializer = new MleapSerializer()

    serializer.addSerializer(StringIndexerModelSerializer)

    serializer.addBundleSerializer(PipelineModelSerializer(serializer))

    serializer
  }
}
