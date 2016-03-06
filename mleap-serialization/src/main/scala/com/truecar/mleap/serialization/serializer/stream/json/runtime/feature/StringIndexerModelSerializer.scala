package com.truecar.mleap.serialization.serializer.stream.json.runtime.feature

import com.truecar.mleap.runtime.transformer.StringIndexerModel
import com.truecar.mleap.serialization.json.CoreJsonSupport._
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object StringIndexerModelSerializer extends JsonStreamSerializer[StringIndexerModel] {
  override val klazz: Class[StringIndexerModel] = classOf[StringIndexerModel]
  override implicit val format = mleapStringIndexerModelFormat
}
