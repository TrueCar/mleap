package com.truecar.mleap.serialization.serializer.stream.json.runtime.feature

import com.truecar.mleap.runtime.transformer.TokenizerModel
import com.truecar.mleap.serialization.json.CoreJsonSupport._
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object TokenizerModelSerializer extends JsonStreamSerializer[TokenizerModel] {
  override val klazz: Class[TokenizerModel] = classOf[TokenizerModel]
  override implicit val format = mleapTokenizerModelFormat
}
