package com.truecar.mleap.serialization.serializer.stream.json.runtime.feature

import com.truecar.mleap.runtime.transformer.StandardScalerModel
import com.truecar.mleap.serialization.json.CoreJsonSupport._
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object StandardScalerModelSerializer extends JsonStreamSerializer[StandardScalerModel] {
  override val klazz: Class[StandardScalerModel] = classOf[StandardScalerModel]
  override implicit val format = mleapStandardScalerModelFormat
}
