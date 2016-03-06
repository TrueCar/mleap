package com.truecar.mleap.serialization.serializer.stream.json.runtime.feature

import com.truecar.mleap.runtime.transformer.HashingTermFrequencyModel
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer
import com.truecar.mleap.serialization.json.CoreJsonSupport._

/**
  * Created by hwilkins on 3/5/16.
  */
object HashingTermFrequencySerializer extends JsonStreamSerializer[HashingTermFrequencyModel] {
  override val klazz: Class[HashingTermFrequencyModel] = classOf[HashingTermFrequencyModel]
  override implicit val format = mleapHashingTermFrequencyModelFormat
}
